package com.gmail.ypon2003.marketplacebackend.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Table(name = "ads")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the ad.", example = "1", required = true)
    private Long ad_id;

    @Schema(description = "Name of the ad.", example = "Bicycle", required = true)
    private String name;

    @Schema(description = "Description of the ad.", example = "A mountain bike in good condition.", required = true)
    private String description;

    @Schema(description = "Price of the ad.", example = "199.99", required = true)
    private DecimalFormat price;

    @Schema(description = "Measurement unit for the ad.", example = "USD", required = true)
    private String measurement;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Creation date of the ad.", example = "2024-05-27T15:35:00")
    private Date createAt;

    @Schema(description = "Information about the seller.", example = "John Doe, phone: +123456789")
    private String infoSeller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @Schema(description = "The person who created the ad.")
    private Person person;

    @ManyToMany(mappedBy = "favorites")
    @Schema(description = "List of persons  who have this ad as a favorite.")
    private List<Person> personList;

}
