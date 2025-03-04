package com.anil.blog.services.impl;

import com.anil.blog.domain.entities.Category;
import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.mappers.CategoryMapper;
import com.anil.blog.repositories.CategoryRepository;
import com.anil.blog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> listCategories() {
        return categoryRepository.findAllWithPostCount()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}
