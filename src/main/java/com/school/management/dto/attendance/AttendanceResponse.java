package com.school.management.dto.attendance;
import com.school.management.entity.AttendanceStatus;
import java.time.LocalDate;
public class AttendanceResponse {
    private Long id, studentId, classId; private String studentName, className;
    private LocalDate date; private AttendanceStatus status; private String comment;
    public AttendanceResponse() {}
    public Long getId() { return id; } public void setId(Long v) { id=v; }
    public Long getStudentId() { return studentId; } public void setStudentId(Long v) { studentId=v; }
    public String getStudentName() { return studentName; } public void setStudentName(String v) { studentName=v; }
    public Long getClassId() { return classId; } public void setClassId(Long v) { classId=v; }
    public String getClassName() { return className; } public void setClassName(String v) { className=v; }
    public LocalDate getDate() { return date; } public void setDate(LocalDate v) { date=v; }
    public AttendanceStatus getStatus() { return status; } public void setStatus(AttendanceStatus v) { status=v; }
    public String getComment() { return comment; } public void setComment(String v) { comment=v; }
}
