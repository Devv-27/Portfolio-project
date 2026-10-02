package com.debabrata.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public class Repos {
    public interface UserRepo extends JpaRepository<Models.User, Long> { Optional<Models.User> findByUsername(String username); }
    public interface AboutRepo extends JpaRepository<Models.About, Long> {}
    public interface SkillRepo extends JpaRepository<Models.Skill, Long> {}
    public interface ProjectRepo extends JpaRepository<Models.Project, Long> {}
    public interface BlogRepo extends JpaRepository<Models.Blog, Long> {}
    public interface ExperienceRepo extends JpaRepository<Models.Experience, Long> {}
    public interface TestimonialRepo extends JpaRepository<Models.Testimonial, Long> {}
    public interface ServiceRepo extends JpaRepository<Models.ServiceItem, Long> {}
    public interface MessageRepo extends JpaRepository<Models.Message, Long> {}
    public interface MediaRepo extends JpaRepository<Models.Media, Long> {}
}
