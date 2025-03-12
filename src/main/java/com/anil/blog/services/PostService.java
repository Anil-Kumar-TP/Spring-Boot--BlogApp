package com.anil.blog.services;

import com.anil.blog.dtos.CreatePostRequest;
import com.anil.blog.dtos.PostDto;
import com.anil.blog.dtos.UpdatePostRequest;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<PostDto> getAllPosts(UUID categoryId, UUID tagId);

    List<PostDto> getDraftPostsForCurrentUser();

    PostDto createPost(CreatePostRequest createPostRequest);

    PostDto updatePost(UUID id, UpdatePostRequest updatePostRequest);
}
