package com.anil.blog.services.impl;

import com.anil.blog.dtos.TagDto;
import com.anil.blog.mappers.TagMapper;
import com.anil.blog.repositories.TagRepository;
import com.anil.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    @Override
    public List<TagDto> getTags() {
        return tagRepository.findAllWithPostCount()
                .stream()
                .map(tagMapper::toTagDto)
                .toList();
    }

}
