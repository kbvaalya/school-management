package com.school.management.dto.user;
import com.school.management.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class CreateUserRequest {
    @NotBlank(message = "Full name is required") private String fullName;
    @NotBlank(message = "Email is required") @Email private String email;
    @NotNull(message = "Role is required") private Role role;
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
}
