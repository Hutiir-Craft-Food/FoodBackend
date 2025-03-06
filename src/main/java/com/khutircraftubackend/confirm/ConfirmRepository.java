package com.khutircraftubackend.confirm;

import com.khutircraftubackend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmRepository extends JpaRepository<ConfirmationEntity, Long> {
    Optional<ConfirmationEntity> findByUser (UserEntity user);
}
