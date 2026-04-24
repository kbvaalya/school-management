package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.attendance.AttendanceFilterRequest;
import com.school.management.dto.attendance.AttendanceResponse;
import com.school.management.dto.attendance.MarkAttendanceRequest;
import com.school.management.dto.attendance.UpdateAttendanceRequest;
import com.school.management.dto.schoolclass.ClassResponse;
import com.school.management.entity.User;
import com.school.management.service.AttendanceService;
import com.school.management.service.SchoolClassService;
import jakarta.validation.Valid;
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

    public ManagerController(AttendanceService attendanceService, SchoolClassService classService) {
        this.attendanceService = attendanceService; this.classService = classService;
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
