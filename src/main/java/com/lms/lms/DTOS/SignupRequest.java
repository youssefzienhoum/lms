package com.lms.lms.DTOS;

import com.lms.lms.Entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
    @Email   
    @NotBlank
    @NotNull
    String email,
    @NotBlank
    @NotNull
    String password,
    @NotBlank
    @NotNull
    String firstname,
    @NotBlank
    @NotNull
    String lastname,
    @NotNull
    User.Role role

) {
}

    // Getters and Setters
    

    