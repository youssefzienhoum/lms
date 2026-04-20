package com.lms.lms.Controller;

import org.springframework.web.bind.annotation.*;
import com.lms.lms.DTOS.CategoryRequest;
import com.lms.lms.Entity.Category;
import com.lms.lms.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

   
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        Category category = categoryService.create(request);

        return ResponseEntity
                .status(201) // CREATED
                .body(category);
    }

   
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Category>> searchCategories(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                categoryService.searchCategories(keyword)
        );
    }
}