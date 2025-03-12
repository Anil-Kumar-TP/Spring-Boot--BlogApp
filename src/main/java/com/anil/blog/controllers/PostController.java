package com.anil.blog.controllers;

import com.anil.blog.dtos.PostDto;
import com.anil.blog.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(@RequestParam(required = false) UUID categoryId,@RequestParam(required = false) UUID tagId){
        List<PostDto> posts = postService.getAllPosts(categoryId,tagId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<PostDto>> getDraftPosts() {
        List<PostDto> draftPosts = postService.getDraftPostsForCurrentUser();
        return ResponseEntity.ok(draftPosts);
    }
}
