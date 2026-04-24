package com.school.management;

import com.school.management.entity.Role;
import com.school.management.entity.User;
import com.school.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SchoolManagementApplication {
    private static final Logger log = LoggerFactory.getLogger(SchoolManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder().username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("System Administrator").email("admin@school.com")
                        .role(Role.ROLE_ADMIN).build();
                userRepository.save(admin);
                log.info("=======================================================");
                log.info("  DEFAULT ADMIN CREATED  |  admin / admin123");
                log.info("=======================================================");
            }
            if (userRepository.findByUsername("manager1").isEmpty()) {
                User manager = User.builder().username("manager1")
                        .password(passwordEncoder.encode("manager123"))
                        .fullName("John Manager").email("manager1@school.com")
                        .role(Role.ROLE_MANAGER).build();
                userRepository.save(manager);
                log.info("  DEFAULT MANAGER CREATED  |  manager1 / manager123");
                log.info("=======================================================");
            }
        };
    }
}
