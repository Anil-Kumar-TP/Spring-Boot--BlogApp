package com.anil.blog.services;

import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.dtos.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> listCategories();

    CategoryDto createCategory(CreateCategoryRequest createCategoryRequest);

}
