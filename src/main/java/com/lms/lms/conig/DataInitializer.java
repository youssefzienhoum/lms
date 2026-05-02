package com.lms.lms.conig;


import com.lms.lms.Entity.User;
import com.lms.lms.Repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initAdmin() {
        return args -> {

            boolean exists = userRepository.findByEmail("admin@test.com").isPresent();

            if (!exists) {
                User admin = new User();
                admin.setEmail("admin@test.com");
                admin.setPassword(passwordEncoder.encode("E177zbT0"));
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setRole(User.Role.ADMIN);
                admin.setActive(true);

                userRepository.save(admin);

                System.out.println(" Admin Created Successfully");
            }
        };
    }
}