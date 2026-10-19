package com.eqel.service;

import com.eqel.entity.RoleEntity;
import com.eqel.entity.UserEntity;
import com.eqel.repository.RoleRepository;
import com.eqel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBootstrapService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${EQEL_ADMIN_BOOTSTRAP_USERNAME:admin}")
    private String bootstrapUsername;

    @Value("${EQEL_ADMIN_BOOTSTRAP_PASSWORD:Admin@eqel2026}")
    private String bootstrapPassword;

    @Value("${EQEL_ADMIN_BOOTSTRAP_EMAIL:admin@eqel.local}")
    private String bootstrapEmail;

    public AdminBootstrapService(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Ensure standard roles exist
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new RoleEntity("ADMIN", "System administrator")));
        roleRepository.findByName("PLACEMENT_OFFICER")
                .orElseGet(() -> roleRepository.save(new RoleEntity("PLACEMENT_OFFICER", "Placement officer")));
        roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new RoleEntity("STUDENT", "Student applicant")));

        // Check if any admin exists
        if (!userRepository.existsByUsername(bootstrapUsername)) {
            UserEntity admin = new UserEntity();
            admin.setUsername(bootstrapUsername);
            admin.setEmail(bootstrapEmail);
            admin.setFullName("System Administrator");
            admin.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
            admin.setRole(adminRole);
            admin.setActive(true);

            userRepository.save(admin);
            log.info("===============================================================");
            log.info("EQEL Bootstrap: Created initial Administrator account '{}'", bootstrapUsername);
            log.info("===============================================================");
        }
    }
}
