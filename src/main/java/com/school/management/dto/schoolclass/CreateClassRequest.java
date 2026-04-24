package com.school.management.dto.schoolclass;
import jakarta.validation.constraints.NotBlank;
public class CreateClassRequest {
    @NotBlank(message = "Class name is required") private String name;
    private String description;
    @NotBlank(message = "Subject is required") private String subject;
    private Long managerId;
    public String getName() { return name; } public void setName(String v) { name=v; }
    public String getDescription() { return description; } public void setDescription(String v) { description=v; }
    public String getSubject() { return subject; } public void setSubject(String v) { subject=v; }
    public Long getManagerId() { return managerId; } public void setManagerId(Long v) { managerId=v; }
}
