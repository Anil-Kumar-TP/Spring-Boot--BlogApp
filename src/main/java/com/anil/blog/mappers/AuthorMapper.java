package com.anil.blog.mappers;

import com.anil.blog.domain.entities.User;
import com.anil.blog.dtos.AuthorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AuthorDto toDto(User user);
}