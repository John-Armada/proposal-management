package com.pointwest.prop.common.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pointwest.prop.accounts.entity.Account;
import com.pointwest.prop.accounts.repository.AccountRepository;
import com.pointwest.prop.auth.model.Role;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.entity.Category;
import com.pointwest.prop.common.entity.Offering;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.common.entity.Template;
import com.pointwest.prop.common.repository.CategoryRepository;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.common.repository.OfferingRepository;
import com.pointwest.prop.templates.repository.TemplateRepository;
import com.pointwest.prop.intake.entity.ProposalRequest;
import com.pointwest.prop.intake.repository.ProposalRequestRepository;
import com.pointwest.prop.proposals.enums.ProposalStatus;
import com.pointwest.prop.proposals.repository.ProposalRepository;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.user.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            OfferingRepository offeringRepository,
            ProposalRequestRepository proposalRequestRepository,
            ProposalRepository proposalRepository,
            CategoryRepository categoryRepository,
            TemplateRepository templateRepository) {
        return args -> {
            // 1. Define Departments
            List<Department> departments;
            if (departmentRepository.count() == 0) {
                departments = List.of(
                        new Department(null, "HR", "Human Resources",
                                "Handles recruiting, onboarding, and employee relations.",
                                true),
                        new Department(null, "IT", "Information Technology",
                                "Manages IT infrastructure, software development, and security.", true),
                        new Department(null, "FIN", "Finance & Accounting",
                                "Oversees financial planning, budgeting, and accounting.", true),
                        new Department(null, "MKT", "Marketing & Sales",
                                "Drives customer acquisition, brand awareness, and revenue.", true),
                        new Department(null, "OPS", "Operations & Logistics",
                                "Coordinates daily operations, supply chain, and workflows.", true));
                departments = departmentRepository.saveAll(departments);
            } else {
                departments = departmentRepository.findAll();
            }

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

            if (userRepository.count() == 0) {
                userRepository.saveAll(usersToInsert);
            }

            Department itDepartment = departmentRepository.findByName("Information Technology")
                    .orElseThrow(() -> new IllegalStateException("Information Technology department was not seeded"));
            User author = userRepository.findByEmailIgnoreCase("human.author1@company.com")
                    .orElseThrow(() -> new IllegalStateException("Seed author was not created"));

            Offering offering = offeringRepository.findByName("Software Development")
                    .orElseGet(() -> offeringRepository.save(new Offering(
                            null,
                            "Software Development",
                            "Custom software development and engineering services.",
                            true)));

            Account account = accountRepository.findByName("Acme Corporation")
                    .orElseGet(() -> accountRepository.save(new Account(
                            null,
                            "Acme Corporation",
                            "Technology",
                            "contact@acme.example")));

            List<Category> categories = List.of(
                    findOrCreateCategory(categoryRepository, "Digital Transformation"),
                    findOrCreateCategory(categoryRepository, "Cloud Services"),
                    findOrCreateCategory(categoryRepository, "Managed Operations"));

            List<Template> templates = new ArrayList<>();
            templates.add(findOrCreateTemplate(templateRepository, "Executive Solution Proposal",
                    "Executive-level proposal for strategic client initiatives."));
            templates.add(findOrCreateTemplate(templateRepository, "Technical Delivery Proposal",
                    "Detailed proposal for software and platform delivery."));
            templates.add(findOrCreateTemplate(templateRepository, "Operations Improvement Proposal",
                    "Proposal for operational efficiency and process improvement."));

            List<Account> accounts = List.of(
                    account,
                    findOrCreateAccount(accountRepository, "Globex Industries", "Manufacturing", "contact@globex.example"),
                    findOrCreateAccount(accountRepository, "Wayne Enterprises", "Financial Services", "contact@wayne.example"));

            List<Offering> offerings = List.of(
                    offering,
                    findOrCreateOffering(offeringRepository, "Cloud Migration",
                            "Cloud migration planning and implementation services."),
                    findOrCreateOffering(offeringRepository, "Business Process Automation",
                            "Automation services for high-volume business processes."));

            List<Department> proposalDepartments = List.of(
                    itDepartment,
                    departmentRepository.findByName("Human Resources")
                            .orElseThrow(() -> new IllegalStateException("Human Resources department was not seeded")),
                    departmentRepository.findByName("Finance & Accounting")
                            .orElseThrow(() -> new IllegalStateException("Finance & Accounting department was not seeded")));

            List<User> authors = List.of(
                    author,
                    userRepository.findByEmailIgnoreCase("human.author1@company.com")
                            .orElseThrow(() -> new IllegalStateException("Seed author was not created")),
                    userRepository.findByEmailIgnoreCase("finance.author1@company.com")
                            .orElseThrow(() -> new IllegalStateException("Seed author was not created")));

            if (proposalRequestRepository.count() == 0) {
                seedProposalRequests(proposalRequestRepository, accounts, offerings,
                        proposalDepartments, authors);
            }

            seedProposals(proposalRepository, proposalRequestRepository, categories, templates);
        };
    }

    private void seedProposals(
            ProposalRepository proposalRepository,
            ProposalRequestRepository proposalRequestRepository,
            List<Category> categories,
            List<Template> templates) {
        List<ProposalRequest> requests = proposalRequestRepository.findAll();
        ProposalStatus[] statuses = {
                ProposalStatus.DRAFT,
                ProposalStatus.IN_REVIEW,
                ProposalStatus.SENT,
                ProposalStatus.WON,
                ProposalStatus.LOST,
                ProposalStatus.APPROVED
        };

                int seededCount = 0;
                for (ProposalRequest request : requests) {
                        if (seededCount >= 12) {
                                break;
                        }

            if (request.getId() == null || proposalRepository.existsByRequestId(request.getId())) {
                continue;
            }

            Proposal proposal = new Proposal();
                        proposal.setTitle("Seed Proposal " + String.format("%02d", seededCount + 1));
            proposal.setDescription("Sample proposal for pagination and filtering tests.");
                        proposal.setStatus(statuses[seededCount % statuses.length]);
            proposal.setCurrentVersion(1);
                        proposal.setGoogleDocUrl("https://docs.google.com/document/d/seed-proposal-" + (seededCount + 1));
                        proposal.setContractValue(BigDecimal.valueOf(10000L + (seededCount * 7500L)));
                        proposal.setProjectDuration(30 + (seededCount * 5));
                        proposal.setTotalResources(3 + (seededCount % 6));
            proposal.setRequest(request);
            proposal.setAccount(request.getAccount());
            proposal.setDepartment(request.getDepartment());
            proposal.setOffering(request.getOffering());
                        proposal.setCategory(categories.get(seededCount % categories.size()));
                        proposal.setTemplate(templates.get(seededCount % templates.size()));

            proposalRepository.save(proposal);
                        seededCount++;
        }
    }

        private void seedProposalRequests(
            ProposalRequestRepository proposalRequestRepository,
            List<Account> accounts,
            List<Offering> offerings,
            List<Department> departments,
                            List<User> authors) {
        for (int setIndex = 0; setIndex < 3; setIndex++) {
            for (int proposalIndex = 1; proposalIndex <= 5; proposalIndex++) {
                Account account = accounts.get(setIndex);
                Offering offering = offerings.get(setIndex);
                Department department = departments.get(setIndex);
                User author = authors.get(setIndex);

                proposalRequestRepository.save(new ProposalRequest(
                        null,
                        "Requirements for " + account.getName() + " proposal " + proposalIndex,
                        LocalDate.now().plusDays(30 + proposalIndex),
                        "OPEN",
                        account,
                        author,
                        department,
                        offering));
            }
        }
    }

    private Category findOrCreateCategory(CategoryRepository repository, String name) {
        return repository.findAll().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> repository.save(new Category(null, name, true)));
    }

    private Template findOrCreateTemplate(TemplateRepository repository, String name, String purpose) {
        return repository.findAll().stream()
                .filter(template -> template.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> repository.save(new Template(null, name, purpose,
                        "https://docs.google.com/document/d/" + name.toLowerCase().replace(' ', '-'))));
    }

    private Account findOrCreateAccount(AccountRepository repository, String name, String industry, String email) {
        return repository.findByNameIgnoreCase(name)
                .orElseGet(() -> repository.save(new Account(null, name, industry, email)));
    }

    private Offering findOrCreateOffering(OfferingRepository repository, String name, String description) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Offering(null, name, description, true)));
    }
}