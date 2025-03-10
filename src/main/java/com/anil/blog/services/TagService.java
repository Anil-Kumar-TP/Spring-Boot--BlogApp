package com.anil.blog.services;

import com.anil.blog.dtos.TagDto;

import java.util.List;

public interface TagService {

    List<TagDto> getTags();
}
