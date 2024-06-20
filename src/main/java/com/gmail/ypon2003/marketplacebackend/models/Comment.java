package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Entity
@Data
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_text")
    private String textComment;

    @Column(name = "comment_timestamp")
    private Timestamp timestamp;

}
