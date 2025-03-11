package com.anil.blog.services;

import com.anil.blog.dtos.CreateTagsRequest;
import com.anil.blog.dtos.TagDto;

import java.util.List;
import java.util.UUID;

public interface TagService {

    List<TagDto> getTags();

    List<TagDto> createTags(CreateTagsRequest createTagsRequest);

    void deleteTag(UUID id);

    TagDto getTagById(UUID id);

}
