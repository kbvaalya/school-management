package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.attendance.AttendanceFilterRequest;
import com.school.management.dto.attendance.AttendanceResponse;
import com.school.management.dto.attendance.MarkAttendanceRequest;
import com.school.management.dto.attendance.UpdateAttendanceRequest;
import com.school.management.dto.schoolclass.ClassResponse;
import com.school.management.dto.schoolclass.CreateClassRequest;
import com.school.management.dto.user.UserResponse;
import com.school.management.entity.Role;
import com.school.management.entity.User;
import com.school.management.service.AttendanceService;
import com.school.management.service.SchoolClassService;
import com.school.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ManagerController {
    private final AttendanceService attendanceService;
    private final SchoolClassService classService;
    private final UserService userService;

    public ManagerController(AttendanceService attendanceService, SchoolClassService classService, UserService userService) {
        this.attendanceService = attendanceService; this.classService = classService; this.userService = userService;
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getMyClasses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String search) {
        List<ClassResponse> result = (search != null && !search.isBlank())
                ? classService.searchClassesByManager(currentUser.getId(), search)
                : classService.getClassesByManager(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClass(@PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.ok(classService.getClassById(classId)));
    }

    @PostMapping("/classes")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(
            @Valid @RequestBody CreateClassRequest request, @AuthenticationPrincipal User currentUser) {
        request.setManagerId(currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Class created", classService.createClass(request)));
    }

    @PostMapping("/classes/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<ClassResponse>> enrollStudent(
            @PathVariable Long classId, @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok("Student enrolled", classService.enrollStudent(classId, studentId)));
    }

    @DeleteMapping("/classes/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<ClassResponse>> removeStudent(
            @PathVariable Long classId, @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok("Student removed", classService.removeStudent(classId, studentId)));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStudents() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUsersByRole(Role.ROLE_USER)));
    }

    // ── Attendance ─────────────────────────────────────────────────────────
    @PostMapping("/attendances")
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance marked", attendanceService.markAttendance(request, currentUser)));
    }

    @PutMapping("/attendances/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id, @Valid @RequestBody UpdateAttendanceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance updated", attendanceService.updateAttendance(id, request, currentUser)));
    }

    @GetMapping("/attendances")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendances(
            AttendanceFilterRequest filter, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.filterAttendanceForManager(filter, currentUser.getId())));
    }
}
