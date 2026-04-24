package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.attendance.AttendanceFilterRequest;
import com.school.management.dto.attendance.AttendanceResponse;
import com.school.management.dto.schoolclass.ClassResponse;
import com.school.management.dto.schoolclass.CreateClassRequest;
import com.school.management.dto.user.CreateUserRequest;
import com.school.management.dto.user.CreateUserResponse;
import com.school.management.dto.user.UserResponse;
import com.school.management.entity.Role;
import com.school.management.service.AttendanceService;
import com.school.management.service.SchoolClassService;
import com.school.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final SchoolClassService classService;
    private final AttendanceService attendanceService;

    public AdminController(UserService userService, SchoolClassService classService, AttendanceService attendanceService) {
        this.userService = userService; this.classService = classService; this.attendanceService = attendanceService;
    }

    // ── Users ──────────────────────────────────────────────────────────────
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created", userService.createUser(request)));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search) {
        List<UserResponse> result;
        if (search != null && !search.isBlank())
            result = role != null ? userService.searchStudents(search) : userService.searchUsers(search);
        else
            result = role != null ? userService.getUsersByRole(role) : userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }

    // ── Classes ────────────────────────────────────────────────────────────
    @PostMapping("/classes")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody CreateClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Class created", classService.createClass(request)));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAllClasses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long managerId) {
        List<ClassResponse> result;
        if (search != null && !search.isBlank()) result = classService.searchClasses(search);
        else if (managerId != null) result = classService.getClassesByManager(managerId);
        else result = classService.getAllClasses();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClass(@PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.ok(classService.getClassById(classId)));
    }

    @PutMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(@PathVariable Long classId, @Valid @RequestBody CreateClassRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Class updated", classService.updateClass(classId, request)));
    }

    @DeleteMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long classId) {
        classService.deleteClass(classId); return ResponseEntity.ok(ApiResponse.ok("Class deleted", null));
    }

    @PatchMapping("/classes/{classId}/manager/{managerId}")
    public ResponseEntity<ApiResponse<ClassResponse>> assignManager(@PathVariable Long classId, @PathVariable Long managerId) {
        return ResponseEntity.ok(ApiResponse.ok("Manager assigned", classService.assignManager(classId, managerId)));
    }

    @PostMapping("/classes/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<ClassResponse>> enrollStudent(@PathVariable Long classId, @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok("Student enrolled", classService.enrollStudent(classId, studentId)));
    }

    @DeleteMapping("/classes/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<ClassResponse>> removeStudent(@PathVariable Long classId, @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok("Student removed", classService.removeStudent(classId, studentId)));
    }

    // ── Attendance ─────────────────────────────────────────────────────────
    @GetMapping("/attendances")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendances(AttendanceFilterRequest filter) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.filterAttendance(filter)));
    }

    @DeleteMapping("/attendances/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id); return ResponseEntity.ok(ApiResponse.ok("Attendance deleted", null));
    }
}
