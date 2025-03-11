package com.anil.blog.repositories;

import com.anil.blog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    //when tags are fetched,its associated posts are also fetched.
    //and in the mapper we calculate the count of those posts that are published.
    //these are then taken care of when we convert it to dtos.
    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.posts")
    List<Tag> findAllWithPostCount();

    List<Tag> findByNameIn(Set<String> names);
}
