package com.anil.blog.mappers;

import com.anil.blog.domain.PostStatus;
import com.anil.blog.domain.entities.Post;
import com.anil.blog.domain.entities.Tag;
import com.anil.blog.dtos.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    @Mapping(target = "postCount",source = "posts",qualifiedByName = "calculatePostCount")
    TagDto toTagDto(Tag tag);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "posts", ignore = true) // Ignore posts as it’s not in TagDto
    Tag toEntity(TagDto dto); // Added for DTO to entity conversion

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts){ //in tag entity it is set.
        if (posts == null){
            return 0;
        }
       return (int) posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
