package com.school.management.repository;

import com.school.management.entity.SchoolClass;
import com.school.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    /** All classes managed by a specific manager */
    List<SchoolClass> findByManager(User manager);

    List<SchoolClass> findByManagerId(Long managerId);

    /** Classes a student is enrolled in */
    @Query("SELECT sc FROM SchoolClass sc JOIN sc.students s WHERE s.id = :studentId")
    List<SchoolClass> findByStudentId(@Param("studentId") Long studentId);

    /** Search by name or subject (case-insensitive) */
    @Query("""
        SELECT sc FROM SchoolClass sc
        WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(sc.subject) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<SchoolClass> searchByNameOrSubject(@Param("query") String query);

    /** Search within classes managed by a manager */
    @Query("""
        SELECT sc FROM SchoolClass sc
        WHERE sc.manager.id = :managerId
          AND (LOWER(sc.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(sc.subject) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    List<SchoolClass> searchByManagerAndQuery(@Param("managerId") Long managerId, @Param("query") String query);
}
