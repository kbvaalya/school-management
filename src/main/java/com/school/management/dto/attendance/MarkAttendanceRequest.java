package com.school.management.dto.attendance;
import com.school.management.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public class MarkAttendanceRequest {
    @NotNull private Long studentId;
    @NotNull private Long classId;
    @NotNull private LocalDate date;
    @NotNull private AttendanceStatus status;
    private String comment;
    public Long getStudentId() { return studentId; } public void setStudentId(Long v) { studentId=v; }
    public Long getClassId() { return classId; } public void setClassId(Long v) { classId=v; }
    public LocalDate getDate() { return date; } public void setDate(LocalDate v) { date=v; }
    public AttendanceStatus getStatus() { return status; } public void setStatus(AttendanceStatus v) { status=v; }
    public String getComment() { return comment; } public void setComment(String v) { comment=v; }
}
