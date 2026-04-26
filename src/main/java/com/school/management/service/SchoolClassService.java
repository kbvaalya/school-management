package com.school.management.service;

import com.school.management.dto.schoolclass.ClassResponse;
import com.school.management.dto.schoolclass.CreateClassRequest;
import com.school.management.dto.user.UserResponse;
import com.school.management.entity.Role;
import com.school.management.entity.SchoolClass;
import com.school.management.entity.User;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.repository.SchoolClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SchoolClassService {
    private static final Logger log = LoggerFactory.getLogger(SchoolClassService.class);
    private final SchoolClassRepository classRepository;
    private final UserService userService;

    public SchoolClassService(SchoolClassRepository classRepository, UserService userService) {
        this.classRepository = classRepository; this.userService = userService;
    }

    @Transactional
    public ClassResponse createClass(CreateClassRequest request) {
        SchoolClass sc = SchoolClass.builder().name(request.getName())
                .description(request.getDescription()).build();
        if (request.getManagerId() != null) {
            User m = userService.findById(request.getManagerId());
            if (m.getRole() != Role.ROLE_MANAGER) throw new BadRequestException("User is not a MANAGER");
            sc.setManager(m);
        }
        SchoolClass saved = classRepository.save(sc);
        log.info("Created class '{}' id={}", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public ClassResponse assignManager(Long classId, Long managerId) {
        SchoolClass sc = findById(classId);
        User m = userService.findById(managerId);
        if (m.getRole() != Role.ROLE_MANAGER) throw new BadRequestException("User is not a MANAGER");
        sc.setManager(m);
        return toResponse(classRepository.save(sc));
    }

    @Transactional
    public ClassResponse enrollStudent(Long classId, Long studentId) {
        SchoolClass sc = findById(classId);
        User s = userService.findById(studentId);
        if (s.getRole() != Role.ROLE_USER) throw new BadRequestException("User is not a student");
        if (sc.getStudents().stream().anyMatch(u -> u.getId().equals(studentId)))
            throw new BadRequestException("Student already enrolled");
        sc.getStudents().add(s);
        return toResponse(classRepository.save(sc));
    }

    @Transactional
    public ClassResponse removeStudent(Long classId, Long studentId) {
        SchoolClass sc = findById(classId);
        boolean removed = sc.getStudents().removeIf(s -> s.getId().equals(studentId));
        if (!removed) throw new BadRequestException("Student not enrolled in this class");
        return toResponse(classRepository.save(sc));
    }

    @Transactional
    public ClassResponse updateClass(Long classId, CreateClassRequest request) {
        SchoolClass sc = findById(classId);
        sc.setName(request.getName()); sc.setDescription(request.getDescription());
        if (request.getManagerId() != null) {
            User m = userService.findById(request.getManagerId());
            if (m.getRole() != Role.ROLE_MANAGER) throw new BadRequestException("User is not a MANAGER");
            sc.setManager(m);
        }
        return toResponse(classRepository.save(sc));
    }

    @Transactional
    public void deleteClass(Long classId) { classRepository.delete(findById(classId)); }

    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long classId) { return toResponse(findById(classId)); }

    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByManager(Long managerId) {
        return classRepository.findByManagerId(managerId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByStudent(Long studentId) {
        return classRepository.findByStudentId(studentId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> searchClasses(String query) {
        return classRepository.searchByNameOrSubject(query).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> searchClassesByManager(Long managerId, String query) {
        return classRepository.searchByManagerAndQuery(managerId, query).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SchoolClass findById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));
    }

    private ClassResponse toResponse(SchoolClass sc) {
        Set<UserResponse> students = sc.getStudents().stream()
                .map(userService::toResponse).collect(Collectors.toSet());
        ClassResponse r = new ClassResponse();
        r.setId(sc.getId()); r.setName(sc.getName()); r.setDescription(sc.getDescription());
        r.setStudentCount(students.size()); r.setStudents(students);
        if (sc.getManager() != null) r.setManager(userService.toResponse(sc.getManager()));
        return r;
    }
}
