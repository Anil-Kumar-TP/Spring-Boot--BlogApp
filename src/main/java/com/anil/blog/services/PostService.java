package com.anil.blog.services;

import com.anil.blog.dtos.PostDto;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<PostDto> getAllPosts(UUID categoryId, UUID tagId);

    List<PostDto> getDraftPostsForCurrentUser();
}
