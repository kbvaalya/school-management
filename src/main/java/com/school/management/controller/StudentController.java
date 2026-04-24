package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.attendance.AttendanceFilterRequest;
import com.school.management.dto.attendance.AttendanceResponse;
import com.school.management.dto.schoolclass.ClassResponse;
import com.school.management.dto.user.UserResponse;
import com.school.management.entity.User;
import com.school.management.exception.AccessDeniedException;
import com.school.management.service.AttendanceService;
import com.school.management.service.SchoolClassService;
import com.school.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
public class StudentController {
    private final UserService userService;
    private final SchoolClassService classService;
    private final AttendanceService attendanceService;

    public StudentController(UserService userService, SchoolClassService classService, AttendanceService attendanceService) {
        this.userService = userService; this.classService = classService; this.attendanceService = attendanceService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(userService.toResponse(currentUser)));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getMyClasses(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(classService.getClassesByStudent(currentUser.getId())));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClass(@PathVariable Long classId, @AuthenticationPrincipal User currentUser) {
        ClassResponse cls = classService.getClassesByStudent(currentUser.getId()).stream()
                .filter(c -> c.getId().equals(classId)).findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not enrolled in class id=" + classId));
        return ResponseEntity.ok(ApiResponse.ok(cls));
    }

    @GetMapping("/attendances")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMyAttendance(
            AttendanceFilterRequest filter, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getMyAttendance(filter, currentUser.getId())));
    }
}
