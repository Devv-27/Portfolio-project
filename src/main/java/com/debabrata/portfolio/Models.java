package com.debabrata.portfolio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** All database tables live here. Fields are public to keep the code short and easy to read. */
public class Models {

    @MappedSuperclass
    public static abstract class Base {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Long id;
    }

    @Entity(name = "User") @Table(name = "users")
    public static class User extends Base {
        @Column(unique = true, nullable = false) public String username;
        @Column(nullable = false) public String passwordHash;
    }

    @Entity(name = "About") @Table(name = "about")
    public static class About extends Base {
        public String fullName, title, email, phone, location, githubUrl, linkedinUrl, imageUrl, resumeUrl, education;
        public Double cgpa;
        @Column(length = 5000) public String bio;
    }

    @Entity(name = "Skill") @Table(name = "skills")
    public static class Skill extends Base {
        public String name, category;
        public Integer proficiency;
        public Integer displayOrder;
    }

    @Entity(name = "Project") @Table(name = "projects")
    public static class Project extends Base {
        public String title, imageUrl, githubUrl, liveUrl, technologies;
        @Column(length = 3000) public String description;
        public boolean featured = false;
        public boolean published = true;
        public Integer displayOrder;
    }

    @Entity(name = "Blog") @Table(name = "blogs")
    public static class Blog extends Base {
        public String title, slug, coverImage;
        @Column(length = 500) public String summary;
        @Column(length = 20000) public String content;
        public boolean published = true;
        public LocalDateTime createdAt = LocalDateTime.now();
    }

    @Entity(name = "Experience") @Table(name = "experience")
    public static class Experience extends Base {
        public String company, role, startDate, endDate, logoUrl;
        @Column(length = 2000) public String description;
        public Integer displayOrder;
    }

    @Entity(name = "Testimonial") @Table(name = "testimonials")
    public static class Testimonial extends Base {
        public String name, designation, photoUrl;
        @Column(length = 1000) public String message;
    }

    @Entity(name = "ServiceItem") @Table(name = "services")
    public static class ServiceItem extends Base {
        public String title, icon;
        @Column(length = 1000) public String description;
    }

    @Entity(name = "Message") @Table(name = "messages")
    public static class Message extends Base {
        public String name, email, subject;
        @Column(length = 5000) public String message;
        public boolean seen = false;
        public LocalDateTime createdAt = LocalDateTime.now();
    }

    @Entity(name = "Media") @Table(name = "media")
    public static class Media extends Base {
        public String fileName, url, contentType;
        public Long sizeBytes;
        public LocalDateTime createdAt = LocalDateTime.now();
    }
}
