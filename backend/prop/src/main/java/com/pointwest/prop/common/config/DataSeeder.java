package com.pointwest.prop.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pointwest.prop.auth.model.Role;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.entity.User;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.common.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(DepartmentRepository departmentRepository, UserRepository userRepository) {
        return args -> {
            if (departmentRepository.count() > 0) {
                return; // Prevent duplicate seeding
            }

            // 1. Define Departments
            List<Department> departments = List.of(
                    new Department(null, "HR", "Human Resources", "Handles recruiting, onboarding, and employee relations.",
                            true),
                    new Department(null, "IT" , "Information Technology",
                            "Manages IT infrastructure, software development, and security.", true),
                    new Department(null, "FIN", "Finance & Accounting",
                            "Oversees financial planning, budgeting, and accounting.", true),
                    new Department(null, "MKT", "Marketing & Sales",
                            "Drives customer acquisition, brand awareness, and revenue.", true),
                    new Department(null, "OPS", "Operations & Logistics",
                            "Coordinates daily operations, supply chain, and workflows.", true));

            departments = departmentRepository.saveAll(departments);

            List<User> usersToInsert = new ArrayList<>();
            Role[] roles = { Role.ADMIN, Role.AUTHOR, Role.REVIEWER };

            // Bcrypt hash example for password: "Password123!"
            String defaultPasswordHash = "$2a$10$Nm3dGNgGV/Hq7yEorD9HoO0fA5duzQEKIPs1DQw0YtX5CB5/Fc50.";

            // 2. Generate 30 Users per Department (10 per Role)
            for (Department dept : departments) {
                String deptCode = dept.getName().split(" ")[0].toLowerCase();

                for (Role role : roles) {
                    for (int i = 1; i <= 10; i++) {
                        String roleCode = role.name().toLowerCase();

                        User user = new User();
                        user.setFirstName(role.name() + i);
                        user.setLastName(dept.getName().split(" ")[0]);
                        user.setEmail(String.format("%s.%s%d@company.com", deptCode, roleCode, i));
                        user.setPasswordHash(defaultPasswordHash);
                        user.setRole(role);
                        user.setDepartment(dept);
                        user.setActive(true);
                        user.setFailedLoginAttempts(0);
                        user.setLockedUntil(null);

                        usersToInsert.add(user);
                    }
                }
            }

            userRepository.saveAll(usersToInsert);
        };
    }
}