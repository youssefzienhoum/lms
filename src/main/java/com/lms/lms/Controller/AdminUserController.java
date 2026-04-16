package com.lms.lms.Controller;

import com.lms.lms.Services.AdminUserService;
import com.lms.lms.DTOS.UserRespones;
import com.lms.lms.DTOS.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // =========================
    // GET ALL USERS
    // =========================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRespones> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    // =========================
    // GET STUDENTS
    // =========================
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRespones> getStudents() {
        return adminUserService.getStudents();
    }

    // =========================
    // GET INSTRUCTORS
    // =========================
    @GetMapping("/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRespones> getInstructors() {
        return adminUserService.getInstructors();
    }

    // =========================
    // BLOCK USER
    // =========================
    @PutMapping("/block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void block(@PathVariable Long id) {
        adminUserService.blockUser(id);
    }

    // =========================
    // UNBLOCK USER
    // =========================
    @PutMapping("/unblock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void unblock(@PathVariable Long id) {
        adminUserService.unblockUser(id);
    }

    // =========================
    // DELETE USER
    // =========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }

    // =========================
    // CHANGE ROLE
    // =========================
  @PutMapping("/role/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void changeRole(@PathVariable Long id,
                       @RequestBody UpdateRoleRequest request) {
    adminUserService.changeRole(id, request.role());
}
}