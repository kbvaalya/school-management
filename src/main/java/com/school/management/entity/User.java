package com.school.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false) @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SchoolClass> enrolledClasses = new HashSet<>();

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SchoolClass> managedClasses = new HashSet<>();

    public User() {}

    // Full constructor for builder pattern
    public User(Long id, String username, String password, String fullName,
                String email, Role role) {
        this.id = id; this.username = username; this.password = password;
        this.fullName = fullName; this.email = email; this.role = role;
    }

    // Static builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username, password, fullName, email;
        private Role role;
        public Builder id(Long id)               { this.id = id; return this; }
        public Builder username(String v)        { this.username = v; return this; }
        public Builder password(String v)        { this.password = v; return this; }
        public Builder fullName(String v)        { this.fullName = v; return this; }
        public Builder email(String v)           { this.email = v; return this; }
        public Builder role(Role v)              { this.role = v; return this; }
        public User build() {
            return new User(id, username, password, fullName, email, role);
        }
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public void setUsername(String username) { this.username = username; }
    public Set<SchoolClass> getEnrolledClasses() { return enrolledClasses; }
    public Set<SchoolClass> getManagedClasses() { return managedClasses; }

    // UserDetails
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
