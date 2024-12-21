package com.khutircraftubackend.blogadvertising;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "image", unique = true, nullable = false)
    private String image;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "creation_date")
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate(){
        creationDate = LocalDateTime.now();
    }
}
