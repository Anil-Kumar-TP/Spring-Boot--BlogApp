package com.anil.blog.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private boolean emailVerified = false;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && email.equals(user.email) && password.equals(user.password) && name.equals(user.name) && createdAt.equals(user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, createdAt);
    }
}
