package com.lms.lms.DTOS;

import com.lms.lms.Entity.User;

import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest (
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
)
 {
    
}
