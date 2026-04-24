package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendances",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_attendance",
        columnNames = {"student_id", "class_id", "date"}))
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AttendanceStatus status;

    @Column(length = 500)
    private String comment;

    public Attendance() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private User student; private SchoolClass schoolClass;
        private LocalDate date; private AttendanceStatus status; private String comment;
        public Builder id(Long v)                  { this.id = v; return this; }
        public Builder student(User v)             { this.student = v; return this; }
        public Builder schoolClass(SchoolClass v)  { this.schoolClass = v; return this; }
        public Builder date(LocalDate v)           { this.date = v; return this; }
        public Builder status(AttendanceStatus v)  { this.status = v; return this; }
        public Builder comment(String v)           { this.comment = v; return this; }
        public Attendance build() {
            Attendance a = new Attendance();
            a.id = id; a.student = student; a.schoolClass = schoolClass;
            a.date = date; a.status = status; a.comment = comment;
            return a;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
