package com.school.management.dto.auth;
import com.school.management.entity.Role;
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String fullName;
    private Role role;
    public AuthResponse() {}
    public AuthResponse(String token, Long userId, String username, String fullName, Role role) {
        this.token = token; this.userId = userId; this.username = username;
        this.fullName = fullName; this.role = role;
    }
    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
}
