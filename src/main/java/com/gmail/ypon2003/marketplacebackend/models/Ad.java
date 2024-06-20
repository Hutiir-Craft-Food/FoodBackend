package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Data
@Table(name = "ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Long ad_id;

    @Column(name = "ad_name")
    private String name;

    @Column(name = "ad_description")
    private String description;

    @Column(name = "ad_price")
    private double price;

    @Column(name = "ad_flag")
    private boolean flag;

    @Column(name = "ad_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "ad_info_seller")
    private String infoSeller;

}
