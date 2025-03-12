package com.anil.blog.controllers;

import com.anil.blog.dtos.CreatePostRequest;
import com.anil.blog.dtos.PostDto;
import com.anil.blog.dtos.UpdatePostRequest;
import com.anil.blog.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Restrict to logged-in users
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostRequest createPostRequest) {
        PostDto createdPost = postService.createPost(createPostRequest);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> updatePost(@PathVariable UUID id, @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        PostDto updatedPost = postService.updatePost(id, updatePostRequest);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
        PostDto post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
