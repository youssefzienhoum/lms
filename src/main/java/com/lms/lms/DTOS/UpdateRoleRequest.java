package com.lms.lms.DTOS;

import com.lms.lms.Entity.User;

public record UpdateRoleRequest(

    Long userId,
       User.Role role

) {
    
}
