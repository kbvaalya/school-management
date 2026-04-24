package com.school.management.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "school_classes")
public class SchoolClass {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "class_students",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<User> students = new HashSet<>();

    public SchoolClass() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String name, description, subject;
        private User manager; private Set<User> students = new HashSet<>();
        public Builder id(Long v)          { this.id = v; return this; }
        public Builder name(String v)      { this.name = v; return this; }
        public Builder description(String v){ this.description = v; return this; }
        public Builder subject(String v)   { this.subject = v; return this; }
        public Builder manager(User v)     { this.manager = v; return this; }
        public SchoolClass build() {
            SchoolClass sc = new SchoolClass();
            sc.id = id; sc.name = name; sc.description = description;
            sc.subject = subject; sc.manager = manager; sc.students = students;
            return sc;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }
    public Set<User> getStudents() { return students; }
    public void setStudents(Set<User> students) { this.students = students; }
}
