package com.school.management.dto.schoolclass;
import com.school.management.dto.user.UserResponse;
import java.util.Set;
public class ClassResponse {
    private Long id; private String name, description, subject;
    private UserResponse manager; private int studentCount; private Set<UserResponse> students;
    public ClassResponse() {}
    public Long getId() { return id; } public void setId(Long v) { id=v; }
    public String getName() { return name; } public void setName(String v) { name=v; }
    public String getDescription() { return description; } public void setDescription(String v) { description=v; }
    public String getSubject() { return subject; } public void setSubject(String v) { subject=v; }
    public UserResponse getManager() { return manager; } public void setManager(UserResponse v) { manager=v; }
    public int getStudentCount() { return studentCount; } public void setStudentCount(int v) { studentCount=v; }
    public Set<UserResponse> getStudents() { return students; } public void setStudents(Set<UserResponse> v) { students=v; }
}
