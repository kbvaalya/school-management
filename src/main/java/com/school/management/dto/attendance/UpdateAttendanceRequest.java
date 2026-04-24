package com.school.management.dto.attendance;
import com.school.management.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
public class UpdateAttendanceRequest {
    @NotNull private AttendanceStatus status;
    private String comment;
    public AttendanceStatus getStatus() { return status; } public void setStatus(AttendanceStatus v) { status=v; }
    public String getComment() { return comment; } public void setComment(String v) { comment=v; }
}
