package com.anil.blog.dtos;

import com.anil.blog.domain.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3,max = 200,message = "Title must be between {min} and {max} characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10,max = 50000,message = "Content must be between {min} and {max} characters")
    private String content;

    @NotNull(message = "CategoryId is required")
    private UUID categoryId;

    @Builder.Default //to avoid null values
    @Size(max = 10,message = "Maximum {max} tags allowed")
    private Set<UUID> tagIds = new HashSet<>();

    @NotNull(message = "Status is required")
    private PostStatus status;
}
