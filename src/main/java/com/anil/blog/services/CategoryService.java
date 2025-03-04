package com.anil.blog.services;

import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.dtos.CreateCategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryDto> listCategories();

    CategoryDto createCategory(CreateCategoryRequest createCategoryRequest);

    void deleteCategory(UUID id);

}
