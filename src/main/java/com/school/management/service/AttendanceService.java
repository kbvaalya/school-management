package com.school.management.service;

import com.school.management.dto.attendance.*;
import com.school.management.entity.*;
import com.school.management.exception.AccessDeniedException;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final SchoolClassService classService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             UserService userService, SchoolClassService classService) {
        this.attendanceRepository = attendanceRepository;
        this.userService = userService; this.classService = classService;
    }

    @Transactional
    public AttendanceResponse markAttendance(MarkAttendanceRequest request, User currentUser) {
        SchoolClass schoolClass = classService.findById(request.getClassId());
        if (currentUser.getRole() == Role.ROLE_MANAGER) requireManagerOwnsClass(currentUser, schoolClass);

        User student = userService.findById(request.getStudentId());
        if (student.getRole() != Role.ROLE_USER) throw new BadRequestException("User is not a student");

        boolean enrolled = schoolClass.getStudents().stream().anyMatch(s -> s.getId().equals(student.getId()));
        if (!enrolled) throw new BadRequestException("Student is not enrolled in this class");

        if (request.getStatus() == AttendanceStatus.ABSENT_EXCUSED &&
                (request.getComment() == null || request.getComment().isBlank()))
            throw new BadRequestException("Comment is required for ABSENT_EXCUSED status");

        Attendance attendance = attendanceRepository
                .findByStudentIdAndSchoolClassIdAndDate(student.getId(), schoolClass.getId(), request.getDate())
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setStudent(student); a.setSchoolClass(schoolClass); a.setDate(request.getDate());
                    return a;
                });
        attendance.setStatus(request.getStatus()); attendance.setComment(request.getComment());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceResponse updateAttendance(Long id, UpdateAttendanceRequest request, User currentUser) {
        Attendance attendance = findById(id);
        if (currentUser.getRole() == Role.ROLE_MANAGER) requireManagerOwnsClass(currentUser, attendance.getSchoolClass());
        if (request.getStatus() == AttendanceStatus.ABSENT_EXCUSED &&
                (request.getComment() == null || request.getComment().isBlank()))
            throw new BadRequestException("Comment is required for ABSENT_EXCUSED status");
        attendance.setStatus(request.getStatus()); attendance.setComment(request.getComment());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> filterAttendance(AttendanceFilterRequest f) {
        return attendanceRepository.filterAttendance(f.getClassId(), f.getStudentId(),
                f.getStatus(), f.getStartDate(), f.getEndDate())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> filterAttendanceForManager(AttendanceFilterRequest f, Long managerId) {
        return attendanceRepository.filterAttendanceByManager(managerId, f.getClassId(),
                f.getStudentId(), f.getStatus(), f.getStartDate(), f.getEndDate())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance(AttendanceFilterRequest f, Long studentId) {
        return attendanceRepository.filterStudentAttendance(studentId, f.getClassId(),
                f.getStatus(), f.getStartDate(), f.getEndDate())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAttendance(Long id) { attendanceRepository.delete(findById(id)); }

    private Attendance findById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));
    }

    private void requireManagerOwnsClass(User manager, SchoolClass sc) {
        if (sc.getManager() == null || !sc.getManager().getId().equals(manager.getId()))
            throw new AccessDeniedException("Manager does not own class id=" + sc.getId());
    }

    private AttendanceResponse toResponse(Attendance a) {
        AttendanceResponse r = new AttendanceResponse();
        r.setId(a.getId()); r.setStudentId(a.getStudent().getId());
        r.setStudentName(a.getStudent().getFullName()); r.setClassId(a.getSchoolClass().getId());
        r.setClassName(a.getSchoolClass().getName()); r.setDate(a.getDate());
        r.setStatus(a.getStatus()); r.setComment(a.getComment());
        return r;
    }
}
