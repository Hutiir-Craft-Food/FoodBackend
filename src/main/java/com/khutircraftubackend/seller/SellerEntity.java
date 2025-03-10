package com.khutircraftubackend.seller;

import com.khutircraftubackend.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Клас SellerEntity є моделлю продавця і відображає таблицю продавців у базі даних.
 */

@Entity
@Table (name = "sellers")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_name")
    private String sellerName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column (name = "creation_date")
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate(){
        creationDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        SellerEntity seller = (SellerEntity) o;
        
        return Objects.equals(id, seller.id) && Objects.equals(sellerName, seller.sellerName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, sellerName);
    }
}