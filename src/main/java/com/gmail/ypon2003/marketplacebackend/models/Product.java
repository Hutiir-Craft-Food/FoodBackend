package com.gmail.ypon2003.marketplacebackend.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the product.", example = "1", required = true)
    private Long id;

    @Schema(description = "Name of the product.", example = "Bicycle", required = true)
    private String name;

    @Schema(description = "Description of the product.", example = "A mountain bike in good condition.", required = true)
    private String description;

    @Schema(description = "Price of the product.", example = "199.99", required = true)
    private BigDecimal price;

    @Schema(description = "Measurement unit for the product.", example = "USD", required = true)
    private String measurement;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Creation date of the product.", example = "2024-05-27T15:35:00")
    private Date createAt;

    @Schema(description = "Information about the seller.", example = "John Doe, phone: +123456789")
    private String infoSeller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @Schema(description = "The person who created the product.")
    private Person person;

    @ManyToMany(mappedBy = "favorites")
    @Schema(description = "List of persons  who have this producrs as a favorites.")
    private List<Person> personList;

}
