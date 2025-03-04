package com.anil.blog.services;

import com.anil.blog.dtos.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> listCategories();
}
