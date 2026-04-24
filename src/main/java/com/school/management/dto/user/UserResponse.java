package com.school.management.dto.user;
import com.school.management.entity.Role;
public class UserResponse {
    private Long id; private String username, fullName, email; private Role role;
    public UserResponse() {}
    public UserResponse(Long id, String username, String fullName, String email, Role role) {
        this.id=id; this.username=username; this.fullName=fullName; this.email=email; this.role=role;
    }
    public Long getId() { return id; } public void setId(Long id) { this.id=id; }
    public String getUsername() { return username; } public void setUsername(String v) { username=v; }
    public String getFullName() { return fullName; } public void setFullName(String v) { fullName=v; }
    public String getEmail() { return email; } public void setEmail(String v) { email=v; }
    public Role getRole() { return role; } public void setRole(Role v) { role=v; }
}
