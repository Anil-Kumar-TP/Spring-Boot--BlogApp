package com.anil.blog.services.impl;

import com.anil.blog.domain.PostStatus;
import com.anil.blog.domain.entities.Post;
import com.anil.blog.dtos.PostDto;
import com.anil.blog.mappers.PostMapper;
import com.anil.blog.repositories.PostRepository;
import com.anil.blog.security.BlogUserDetails;
import com.anil.blog.services.CategoryService;
import com.anil.blog.services.PostService;
import com.anil.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CategoryService categoryService;

    private final TagService tagService;

    private final PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts(UUID categoryId, UUID tagId) {

        //check for valid id,would not proceed otherwise
        if (categoryId != null) {
            categoryService.getCategoryById(categoryId); // Throws if not found
        }

        if (tagId != null) {
            tagService.getTagById(tagId); // Throws if not found
        }

        List<Post> posts;
        if (categoryId != null && tagId != null) {
            posts = postRepository.findByCategoryIdAndTagIdAndPostStatus(categoryId, tagId, PostStatus.PUBLISHED);
        } else if (categoryId != null) {
            posts = postRepository.findByCategoryIdAndPostStatus(categoryId, PostStatus.PUBLISHED);
        } else if (tagId != null) {
            posts = postRepository.findByTagIdAndPostStatus(tagId, PostStatus.PUBLISHED);
        } else {
            posts = postRepository.findByPostStatus(PostStatus.PUBLISHED);
        }

        return posts.stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getDraftPostsForCurrentUser() {
        BlogUserDetails userDetails = (BlogUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UUID authorId = userDetails.getId();
        List<Post> draftPosts = postRepository.findByStatusAndAuthorId(PostStatus.DRAFT, authorId);
        return draftPosts.stream().map(postMapper::toDto).toList();
    }
}
