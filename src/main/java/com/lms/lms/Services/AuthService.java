package com.lms.lms.Services;

import com.lms.lms.DTOS.LoginRequest;
import com.lms.lms.DTOS.SignupRequest;
import com.lms.lms.Entity.User;
import com.lms.lms.Repo.UserRepository;
import com.lms.lms.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // REGISTER
    // =========================
    public String register(SignupRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstname());
        user.setLastName(request.lastname());
        user.setRole(request.role());
        user.setActive(true);

        userRepository.save(user);

        // 🔥 FIX: include role in token
        return jwtUtils.generateToken(user.getEmail(), user.getRole().name());
    }

    // =========================
    // LOGIN
    // =========================
    public String login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 FIX: include role in token
        return jwtUtils.generateToken(user.getEmail(), user.getRole().name());
    }
}