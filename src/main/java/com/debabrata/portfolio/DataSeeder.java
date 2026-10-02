package com.debabrata.portfolio;

import com.debabrata.portfolio.Models.*;
import com.debabrata.portfolio.Repos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Runs on startup: creates the admin login and starter content if the database is empty. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepo users;
    private final AboutRepo about;
    private final SkillRepo skills;
    private final PasswordEncoder encoder;
    @Value("${app.admin.username}") private String adminUser;
    @Value("${app.admin.password}") private String adminPass;

    public DataSeeder(UserRepo users, AboutRepo about, SkillRepo skills, PasswordEncoder encoder) {
        this.users = users; this.about = about; this.skills = skills; this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            User u = new User();
            u.username = adminUser;
            u.passwordHash = encoder.encode(adminPass);
            users.save(u);
            System.out.println(">>> Admin created. Username: " + adminUser + "  (change the password before deploying!)");
        }
        if (about.count() == 0) {
            About a = new About();
            a.fullName = "Debabrata Kamila";
            a.title = "Java Full Stack Developer";
            a.cgpa = 8.6;
            a.bio = "Hi, I'm Debabrata Kamila. I build clean, practical web applications with Java, Spring Boot and React.";
            about.save(a);
        }
        if (skills.count() == 0) {
            addSkill("Java", "Backend", 85, 1);
            addSkill("Spring Boot", "Backend", 80, 2);
            addSkill("PostgreSQL", "Database", 75, 3);
            addSkill("React", "Frontend", 75, 4);
            addSkill("REST APIs", "Backend", 80, 5);
        }
    }

    private void addSkill(String name, String category, int level, int order) {
        Skill s = new Skill();
        s.name = name; s.category = category; s.proficiency = level; s.displayOrder = order;
        skills.save(s);
    }
}
