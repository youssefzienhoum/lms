package com.lms.lms.Controller;

import com.lms.lms.Services.AdminUserService;
import com.lms.lms.DTOS.UserRespones;
import com.lms.lms.DTOS.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
   
    public List<UserRespones> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    
    @GetMapping("/students")
    public List<UserRespones> getStudents() {
        return adminUserService.getStudents();
    }

   
    @GetMapping("/instructors")
   
    public List<UserRespones> getInstructors() {
        return adminUserService.getInstructors();
    }

    
    @PutMapping("/block/{id}")
    public void block(@PathVariable Long id) {
        adminUserService.blockUser(id);
    }

 
    @PutMapping("/unblock/{id}")
   
    public void unblock(@PathVariable Long id) {
        adminUserService.unblockUser(id);
    }

    
    @DeleteMapping("/delete/{id}")
   
    public void delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }

   
  @PutMapping("/role/{id}")

public void changeRole(@PathVariable Long id,
                       @RequestBody UpdateRoleRequest request) {
    adminUserService.changeRole(id, request.role());
}
}