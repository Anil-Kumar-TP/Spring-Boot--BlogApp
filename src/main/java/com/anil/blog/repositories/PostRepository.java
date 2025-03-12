package com.anil.blog.repositories;

import com.anil.blog.domain.PostStatus;
import com.anil.blog.domain.entities.Category;
import com.anil.blog.domain.entities.Post;
import com.anil.blog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p " +
            "WHERE p.status = :status " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:tagId IS NULL OR EXISTS (SELECT t FROM p.tags t WHERE t.id = :tagId))")
    List<Post> findByCategoryIdAndTagIdAndPostStatus(UUID categoryId, UUID tagId, PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.category.id = :categoryId")
    List<Post> findByCategoryIdAndPostStatus(UUID categoryId, PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND EXISTS (SELECT t FROM p.tags t WHERE t.id = :tagId)")
    List<Post> findByTagIdAndPostStatus(UUID tagId, PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status")
    List<Post> findByPostStatus(PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.author.id = :authorId")
    List<Post> findByStatusAndAuthorId(
            @Param("status") PostStatus status,
            @Param("authorId") UUID authorId);
}
