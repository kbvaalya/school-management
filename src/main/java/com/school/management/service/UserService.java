package com.school.management.service;

import com.school.management.dto.user.CreateUserRequest;
import com.school.management.dto.user.CreateUserResponse;
import com.school.management.dto.user.UserResponse;
import com.school.management.entity.Role;
import com.school.management.entity.User;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (request.getRole() == Role.ROLE_ADMIN)
            throw new BadRequestException("Cannot create another ADMIN through this endpoint");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already in use: " + request.getEmail());

        String baseUsername = generateUsername(request.getFullName());
        String username = ensureUniqueUsername(baseUsername);
        String rawPassword = generatePassword();

        User user = User.builder()
                .username(username).password(passwordEncoder.encode(rawPassword))
                .fullName(request.getFullName()).email(request.getEmail()).role(request.getRole())
                .build();
        User saved = userRepository.save(user);
        log.info("Created user '{}' with role {}", username, request.getRole());

        CreateUserResponse resp = new CreateUserResponse();
        resp.setId(saved.getId()); resp.setUsername(username);
        resp.setGeneratedPassword(rawPassword); resp.setFullName(saved.getFullName());
        resp.setEmail(saved.getEmail()); resp.setRole(saved.getRole());
        resp.setMessage("User created. Share these credentials with the user.");
        return resp;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchAll(query).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> searchStudents(String query) {
        return userRepository.searchByRoleAndQuery(Role.ROLE_USER, query).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) { return toResponse(findById(id)); }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        if (user.getRole() == Role.ROLE_ADMIN) throw new BadRequestException("Cannot delete admin account");
        userRepository.delete(user);
        log.info("Deleted user id={}", id);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole());
    }

    private String generateUsername(String fullName) {
        String base = fullName.toLowerCase().replaceAll("\\s+", ".").replaceAll("[^a-z0-9.]", "");
        return base.length() > 20 ? base.substring(0, 20) : base;
    }

    private String ensureUniqueUsername(String base) {
        String candidate = base; int suffix = 1;
        while (userRepository.existsByUsername(candidate)) candidate = base + suffix++;
        return candidate;
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        return sb.toString();
    }
}
