package com.lms.lms.Services;

import com.lms.lms.DTOS.CategoryRequest;
import com.lms.lms.Entity.Category;
import com.lms.lms.Repo.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {


        return categoryRepository.findAll();
    }

    public Category create(CategoryRequest request) {

        if (categoryRepository.findByName(request.name()) .isPresent()) {
            throw new RuntimeException("Category already exists");
        }
        categoryRepository.findByName(request.name()).ifPresent(category -> {
            throw new RuntimeException("Category already exists");
        });
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setIcon(request.icon());
    

        return categoryRepository.save(category);
    }

    
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }
}