package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.attendance.AttendanceFilterRequest;
import com.school.management.dto.attendance.AttendanceResponse;
import com.school.management.dto.attendance.MarkAttendanceRequest;
import com.school.management.dto.attendance.UpdateAttendanceRequest;
import com.school.management.entity.Role;
import com.school.management.entity.User;
import com.school.management.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendances(
            AttendanceFilterRequest filter, @AuthenticationPrincipal User currentUser) {
        List<AttendanceResponse> responses;
        if (currentUser.getRole() == Role.ROLE_USER) {
            responses = attendanceService.getMyAttendance(filter, currentUser.getId());
        } else if (currentUser.getRole() == Role.ROLE_MANAGER) {
            responses = attendanceService.filterAttendanceForManager(filter, currentUser.getId());
        } else {
            responses = attendanceService.filterAttendance(filter);
        }
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance marked", attendanceService.markAttendance(request, currentUser)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id, @Valid @RequestBody UpdateAttendanceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance updated", attendanceService.updateAttendance(id, request, currentUser)));
    }
}
