package com.anil.blog.controllers;

import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.dtos.CreateCategoryRequest;
import com.anil.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categories = categoryService.listCategories();
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        CategoryDto categoryDto = categoryService.createCategory(createCategoryRequest);
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }
}
