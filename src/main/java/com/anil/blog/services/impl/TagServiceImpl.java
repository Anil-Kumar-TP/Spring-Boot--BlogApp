package com.anil.blog.services.impl;

import com.anil.blog.domain.entities.Category;
import com.anil.blog.domain.entities.Tag;
import com.anil.blog.dtos.CreateTagsRequest;
import com.anil.blog.dtos.TagDto;
import com.anil.blog.mappers.TagMapper;
import com.anil.blog.repositories.TagRepository;
import com.anil.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public List<TagDto> createTags(CreateTagsRequest createTagsRequest) {
        Set<String> tagNames = createTagsRequest.getNames();

        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);//get the Tag entity
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());//from the list we only need TagNames.

        // Create new tags for names that don’t exist
        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> Tag.builder().name(name).build())
                .map(tagRepository::save)
                .toList();

        // Combine existing and new tags and return as DTOs
        existingTags.addAll(newTags);
        return existingTags.stream()
                .map(tagMapper::toTagDto)
                .toList();
    }

    @Override
    public void deleteTag(UUID id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isPresent()){
            if (tag.get().getPosts().size() > 0){ //has post in the tag.cant delete.
                throw new IllegalStateException("Tag has posts associated with it.");
            }
            tagRepository.deleteById(id);
        }
    }

    @Override
    public TagDto getTagById(UUID id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No tag found with this id" + id));
        return tagMapper.toTagDto(tag);
    }
}
