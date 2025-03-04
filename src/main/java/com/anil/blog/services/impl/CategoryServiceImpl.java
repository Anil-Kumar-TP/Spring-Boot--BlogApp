package com.anil.blog.services.impl;

import com.anil.blog.domain.entities.Category;
import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.dtos.CreateCategoryRequest;
import com.anil.blog.mappers.CategoryMapper;
import com.anil.blog.repositories.CategoryRepository;
import com.anil.blog.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest) {
        String categoryName = createCategoryRequest.getName();
        if (categoryRepository.existsByNameIgnoreCase(categoryName)){
            throw new IllegalArgumentException("Category already exist with name " + categoryName);
        }
        Category category = categoryMapper.toEntity(createCategoryRequest);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()){
            if (category.get().getPosts().size() > 0){ //has post in the category.cant delete.
                throw new IllegalStateException("Category has posts associated with it.");
            }
            categoryRepository.deleteById(id);
        }
    }
}
