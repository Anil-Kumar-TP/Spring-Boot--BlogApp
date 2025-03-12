package com.anil.blog.services.impl;

import com.anil.blog.domain.PostStatus;
import com.anil.blog.domain.entities.Category;
import com.anil.blog.domain.entities.Post;
import com.anil.blog.domain.entities.Tag;
import com.anil.blog.domain.entities.User;
import com.anil.blog.dtos.*;
import com.anil.blog.mappers.CategoryMapper;
import com.anil.blog.mappers.PostMapper;
import com.anil.blog.mappers.TagMapper;
import com.anil.blog.repositories.PostRepository;
import com.anil.blog.repositories.UserRepository;
import com.anil.blog.security.BlogUserDetails;
import com.anil.blog.services.CategoryService;
import com.anil.blog.services.PostService;
import com.anil.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CategoryService categoryService;

    private final TagService tagService;

    private final PostMapper postMapper;

    private final CategoryMapper categoryMapper;

    private final TagMapper tagMapper;
    private final UserRepository userRepository;

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
        // Ensures only drafts where author_id matches the authenticated user's ID are returned
        List<Post> draftPosts = postRepository.findByStatusAndAuthorId(PostStatus.DRAFT, authorId);
        return draftPosts.stream().map(postMapper::toDto).toList();
    }

    @Override
    public PostDto createPost(CreatePostRequest createPostRequest) {
        BlogUserDetails userDetails = (BlogUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UUID authorId = userDetails.getId();

        // Fetch the existing User entity
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + authorId));

        CategoryDto categoryDto = categoryService.getCategoryById(createPostRequest.getCategoryId());
        Category category = categoryMapper.toEntity(categoryDto);

        Set<Tag> tags = createPostRequest.getTagIds().stream()
                .map(tagId -> {
                    TagDto tagDto = tagService.getTagById(tagId);
                    return tagMapper.toEntity(tagDto);
                })
                .collect(Collectors.toSet());

        Post post = Post.builder()
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .status(createPostRequest.getStatus())
                .readingTime(calculateReadingTime(createPostRequest.getContent()))
                .author(author) // Use the fetched User entity
                .category(category)
                .tags(tags)
                .build();

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    private int calculateReadingTime(String content) {
        int wordCount = content.split("\\s+").length;
        return Math.max(1, wordCount / 200);
    }

    @Override
    @Transactional
    public PostDto updatePost(UUID id, UpdatePostRequest updatePostRequest) {
        // Get current authenticated user
        BlogUserDetails userDetails = (BlogUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UUID currentUserId = userDetails.getId();

        // Fetch existing post using the path id
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + id));

        // Verify the current user is the author
        if (!existingPost.getAuthor().getId().equals(currentUserId)) {
            throw new SecurityException("You can only update your own posts");
        }

        // Fetch category
        CategoryDto categoryDto = categoryService.getCategoryById(updatePostRequest.getCategoryId());
        Category category = categoryMapper.toEntity(categoryDto);

        // Fetch tags
        Set<Tag> tags = updatePostRequest.getTagIds().stream()
                .map(tagId -> {
                    TagDto tagDto = tagService.getTagById(tagId);
                    return tagMapper.toEntity(tagDto);
                })
                .collect(Collectors.toSet());

        // Update post fields
        existingPost.setTitle(updatePostRequest.getTitle());
        existingPost.setContent(updatePostRequest.getContent());
        existingPost.setCategory(category);
        existingPost.setTags(tags);
        existingPost.setStatus(updatePostRequest.getPostStatus());
        existingPost.setReadingTime(calculateReadingTime(updatePostRequest.getContent()));

        // Save updated post
        Post updatedPost = postRepository.save(existingPost);
        return postMapper.toDto(updatedPost);
    }
}
