package com.school.management.dto.user;
import com.school.management.entity.Role;
public class CreateUserResponse {
    private Long id; private String username, generatedPassword, fullName, email, message; private Role role;
    public CreateUserResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id=id; }
    public String getUsername() { return username; } public void setUsername(String v) { username=v; }
    public String getGeneratedPassword() { return generatedPassword; } public void setGeneratedPassword(String v) { generatedPassword=v; }
    public String getFullName() { return fullName; } public void setFullName(String v) { fullName=v; }
    public String getEmail() { return email; } public void setEmail(String v) { email=v; }
    public Role getRole() { return role; } public void setRole(Role v) { role=v; }
    public String getMessage() { return message; } public void setMessage(String v) { message=v; }
}
