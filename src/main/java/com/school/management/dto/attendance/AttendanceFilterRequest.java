package com.school.management.dto.attendance;
import com.school.management.entity.AttendanceStatus;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
public class AttendanceFilterRequest {
    private Long classId; private Long studentId; private AttendanceStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate endDate;
    public Long getClassId() { return classId; } public void setClassId(Long v) { classId=v; }
    public Long getStudentId() { return studentId; } public void setStudentId(Long v) { studentId=v; }
    public AttendanceStatus getStatus() { return status; } public void setStatus(AttendanceStatus v) { status=v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { startDate=v; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { endDate=v; }
}
