package com.school.management.repository;

import com.school.management.entity.Role;
import com.school.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    /** Search students by name or username (case-insensitive) */
    @Query("""
        SELECT u FROM User u
        WHERE u.role = :role
          AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    List<User> searchByRoleAndQuery(@Param("role") Role role, @Param("query") String query);

    /** Find all users (any role) matching a name/username filter */
    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<User> searchAll(@Param("query") String query);
}
