package com.school.management.repository;

import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSchoolClassIdAndDate(
            Long studentId, Long classId, LocalDate date);

    /** All attendance records for a student */
    List<Attendance> findByStudentId(Long studentId);

    /** All attendance records for a class */
    List<Attendance> findBySchoolClassId(Long classId);

    /**
     * Flexible filter — all params are optional (pass null to skip).
     * Used by both ADMIN and MANAGER endpoints.
     */
    @Query("""
        SELECT a FROM Attendance a
        WHERE (:classId   IS NULL OR a.schoolClass.id = :classId)
          AND (:studentId IS NULL OR a.student.id     = :studentId)
          AND (:status    IS NULL OR a.status         = :status)
          AND (:startDate IS NULL OR a.date           >= :startDate)
          AND (:endDate   IS NULL OR a.date           <= :endDate)
        ORDER BY a.date DESC
    """)
    List<Attendance> filterAttendance(
            @Param("classId")   Long classId,
            @Param("studentId") Long studentId,
            @Param("status")    AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    /**
     * Filter restricted to classes managed by a specific manager.
     */
    @Query("""
        SELECT a FROM Attendance a
        WHERE a.schoolClass.manager.id = :managerId
          AND (:classId   IS NULL OR a.schoolClass.id = :classId)
          AND (:studentId IS NULL OR a.student.id     = :studentId)
          AND (:status    IS NULL OR a.status         = :status)
          AND (:startDate IS NULL OR a.date           >= :startDate)
          AND (:endDate   IS NULL OR a.date           <= :endDate)
        ORDER BY a.date DESC
    """)
    List<Attendance> filterAttendanceByManager(
            @Param("managerId") Long managerId,
            @Param("classId")   Long classId,
            @Param("studentId") Long studentId,
            @Param("status")    AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    /**
     * Student's own attendance with optional filters.
     */
    @Query("""
        SELECT a FROM Attendance a
        WHERE a.student.id = :studentId
          AND (:classId   IS NULL OR a.schoolClass.id = :classId)
          AND (:status    IS NULL OR a.status         = :status)
          AND (:startDate IS NULL OR a.date           >= :startDate)
          AND (:endDate   IS NULL OR a.date           <= :endDate)
        ORDER BY a.date DESC
    """)
    List<Attendance> filterStudentAttendance(
            @Param("studentId") Long studentId,
            @Param("classId")   Long classId,
            @Param("status")    AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );
}
