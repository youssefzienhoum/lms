package com.lms.lms.Services;

import org.springframework.stereotype.Service;

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


@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    
    
    public String register(SignupRequest request) {
      
        // Check if user exists
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
        return jwtUtils.generateToken(request.email() );
    }
    
    public String login(LoginRequest request) {
        @SuppressWarnings("unused")
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        
        return jwtUtils.generateToken(request.email());
    }
}
