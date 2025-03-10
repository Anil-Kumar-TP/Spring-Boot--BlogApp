package com.anil.blog.controllers;

import com.anil.blog.dtos.TagDto;
import com.anil.blog.services.CategoryService;
import com.anil.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(){
        List<TagDto> tags = tagService.getTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }
}
