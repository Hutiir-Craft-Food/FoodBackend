package com.khutircraftubackend.seller;

import com.khutircraftubackend.Auditable;
import com.khutircraftubackend.address.AddressEntity;
import com.khutircraftubackend.delivery.DeliveryMethodEntity;
import com.khutircraftubackend.seller.qualityCertificates.QualityCertificateEntity;
import com.khutircraftubackend.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
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
public class SellerEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "logo")
    private String logoUrl;
    
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;
    
    @Column(name = "customer_phone_number", nullable = false, unique = true)
    private String customerPhoneNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private AddressEntity address;
    
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Collection<DeliveryMethodEntity> deliveryMethods;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<QualityCertificateEntity> qualityCertificatesUrl = new ArrayList<>();
    
    public void addCertificate(QualityCertificateEntity certificate) {
        qualityCertificatesUrl.add(certificate);
        certificate.setSeller(this);
    }
    
    public void removeCertificate(QualityCertificateEntity certificate) {
        qualityCertificatesUrl.remove(certificate);
        certificate.setSeller(null);
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        SellerEntity seller = (SellerEntity) o;
        
        return Objects.equals(id, seller.id) && Objects.equals(companyName, seller.companyName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, companyName);
    }
}