package com.anil.blog.mappers;

import com.anil.blog.domain.entities.Post;
import com.anil.blog.dtos.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {AuthorMapper.class, CategoryMapper.class, TagMapper.class})
public interface PostMapper {

    @Mapping(target = "author",source = "author")
    @Mapping(target = "category",source = "category")
    @Mapping(target = "tags",source = "tags")
    PostDto toDto(Post post);
}
