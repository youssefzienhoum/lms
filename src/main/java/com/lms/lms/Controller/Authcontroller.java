package com.lms.lms.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.DTOS.LoginRequest;
import com.lms.lms.DTOS.SignupRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.lms.lms.Services.AuthService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor    
public class Authcontroller {
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody @Valid SignupRequest request) {
        String token = authService.register(request);   
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/login")
    
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
    
