package com.khutircraftubackend.marketing;

import com.khutircraftubackend.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "marketing_campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketingCampaignEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "subscribed")
    private boolean isSubscribed;

    @Column (name = "category")
    private String category;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
