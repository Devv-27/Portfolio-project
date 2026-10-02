package com.debabrata.portfolio;

import com.debabrata.portfolio.Models.*;
import com.debabrata.portfolio.Repos.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Every REST endpoint of the CMS. */
public class Api {

    // ---------- Generic CRUD: GET, GET/{id}, POST, PUT/{id}, DELETE/{id} ----------
    public static abstract class CrudController<T extends Base> {
        protected final JpaRepository<T, Long> repo;
        protected CrudController(JpaRepository<T, Long> repo) { this.repo = repo; }

        @GetMapping
        public List<T> all() { return repo.findAll(); }

        @GetMapping("/{id}")
        public ResponseEntity<T> one(@PathVariable Long id) {
            Optional<T> found = repo.findById(id);
            if (found.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(found.get());
        }

        @PostMapping
        public T create(@RequestBody T body) { body.id = null; return repo.save(body); }

        @PutMapping("/{id}")
        public ResponseEntity<T> update(@PathVariable Long id, @RequestBody T body) {
            if (!repo.existsById(id)) return ResponseEntity.notFound().build();
            body.id = id;
            return ResponseEntity.ok(repo.save(body));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            if (!repo.existsById(id)) return ResponseEntity.notFound().build();
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }

    // ---------- Content types ----------
    @RestController @RequestMapping("/api/skills")
    public static class SkillsApi extends CrudController<Skill> { public SkillsApi(SkillRepo r) { super(r); } }

    @RestController @RequestMapping("/api/projects")
    public static class ProjectsApi extends CrudController<Project> { public ProjectsApi(ProjectRepo r) { super(r); } }

    @RestController @RequestMapping("/api/blogs")
    public static class BlogsApi extends CrudController<Blog> { public BlogsApi(BlogRepo r) { super(r); } }

    @RestController @RequestMapping("/api/experience")
    public static class ExperienceApi extends CrudController<Experience> { public ExperienceApi(ExperienceRepo r) { super(r); } }

    @RestController @RequestMapping("/api/testimonials")
    public static class TestimonialsApi extends CrudController<Testimonial> { public TestimonialsApi(TestimonialRepo r) { super(r); } }

    @RestController @RequestMapping("/api/services")
    public static class ServicesApi extends CrudController<ServiceItem> { public ServicesApi(ServiceRepo r) { super(r); } }

    @RestController @RequestMapping("/api/messages")   // admin only (see SecurityConfig)
    public static class MessagesApi extends CrudController<Message> { public MessagesApi(MessageRepo r) { super(r); } }

    @RestController @RequestMapping("/api/media")
    public static class MediaApi extends CrudController<Media> { public MediaApi(MediaRepo r) { super(r); } }

    // ---------- About: only one record, so just GET and PUT ----------
    @RestController @RequestMapping("/api/about")
    public static class AboutApi {
        private final AboutRepo repo;
        public AboutApi(AboutRepo repo) { this.repo = repo; }

        @GetMapping
        public About get() { return repo.findAll().stream().findFirst().orElse(new About()); }

        @PutMapping
        public About put(@RequestBody About body) {
            About current = repo.findAll().stream().findFirst().orElse(null);
            body.id = current == null ? null : current.id;
            return repo.save(body);
        }
    }

    // ---------- Auth ----------
    @RestController @RequestMapping("/api/auth")
    public static class AuthApi {
        private final UserRepo users;
        private final PasswordEncoder encoder;
        private final Jwt jwt;
        public AuthApi(UserRepo users, PasswordEncoder encoder, Jwt jwt) {
            this.users = users; this.encoder = encoder; this.jwt = jwt;
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
            User u = users.findByUsername(body.getOrDefault("username", "")).orElse(null);
            if (u == null || !encoder.matches(body.getOrDefault("password", ""), u.passwordHash)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }
            return ResponseEntity.ok(Map.of("token", jwt.create(u.username), "username", u.username));
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestHeader(value = "Authorization", required = false) String header) {
            String user = (header != null && header.startsWith("Bearer ")) ? jwt.read(header.substring(7)) : null;
            if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            return ResponseEntity.ok(Map.of("token", jwt.create(user)));
        }
    }

    // ---------- Image upload ----------
    @RestController @RequestMapping("/api/upload")
    public static class UploadApi {
        private final MediaRepo media;
        @Value("${app.upload-dir}") private String uploadDir;
        public UploadApi(MediaRepo media) { this.media = media; }

        @PostMapping("/image")
        public ResponseEntity<?> image(@RequestParam("file") MultipartFile file) throws IOException {
            String type = file.getContentType();
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            ext = ext == null ? "" : ext.toLowerCase();
            if (file.isEmpty() || type == null || !type.startsWith("image/")
                    || !List.of("jpg", "jpeg", "png", "gif", "webp").contains(ext)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Upload a jpg, png, gif or webp image (max 5MB)"));
            }
            Path folder = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(folder);
            String name = UUID.randomUUID() + "." + ext;
            Files.copy(file.getInputStream(), folder.resolve(name), StandardCopyOption.REPLACE_EXISTING);

            Media m = new Media();
            m.fileName = name; m.url = "/uploads/" + name; m.contentType = type; m.sizeBytes = file.getSize();
            media.save(m);
            return ResponseEntity.ok(Map.of("url", m.url));
        }
    }

    // ---------- Contact form: save in DB + send email (if configured) ----------
    @RestController @RequestMapping("/api/contact")
    public static class ContactApi {
        private final MessageRepo repo;
        private final ObjectProvider<JavaMailSender> mail;
        @Value("${app.contact.to:}") private String to;
        public ContactApi(MessageRepo repo, ObjectProvider<JavaMailSender> mail) { this.repo = repo; this.mail = mail; }

        @PostMapping
        public ResponseEntity<?> send(@RequestBody Message m) {
            if (isBlank(m.name) || isBlank(m.email) || isBlank(m.message)) {
                return ResponseEntity.badRequest().body(Map.of("error", "name, email and message are required"));
            }
            m.id = null; m.seen = false; m.createdAt = LocalDateTime.now();
            repo.save(m);

            JavaMailSender sender = mail.getIfAvailable();
            if (sender != null && !isBlank(to)) {
                try {
                    SimpleMailMessage mm = new SimpleMailMessage();
                    mm.setTo(to);
                    mm.setReplyTo(m.email);
                    mm.setSubject("Portfolio message: " + (m.subject == null ? "" : m.subject));
                    mm.setText("From: " + m.name + " <" + m.email + ">\n\n" + m.message);
                    sender.send(mm);
                } catch (Exception e) {
                    System.out.println("Email not sent (message is saved in DB): " + e.getMessage());
                }
            }
            return ResponseEntity.ok(Map.of("status", "sent"));
        }

        private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    }
}
