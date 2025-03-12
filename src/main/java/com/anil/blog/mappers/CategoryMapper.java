package com.anil.blog.mappers;

import com.anil.blog.domain.PostStatus;
import com.anil.blog.domain.entities.Category;
import com.anil.blog.domain.entities.Post;
import com.anil.blog.dtos.CategoryDto;
import com.anil.blog.dtos.CreateCategoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount",source = "posts",qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true) // ID is generated, ignore it
    @Mapping(target = "name", source = "name")
    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "posts", ignore = true) // Ignore posts as it’s not in CategoryDto
    Category toEntity(CategoryDto dto); // Added for DTO to entity conversion

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts){

        if (posts == null){
            return 0;
        }
        return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
