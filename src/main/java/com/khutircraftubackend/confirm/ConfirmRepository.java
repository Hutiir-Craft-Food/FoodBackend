package com.khutircraftubackend.confirm;

import com.khutircraftubackend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmRepository extends JpaRepository<ConfirmEntity, Long> {
    Optional<ConfirmEntity> findByUser (UserEntity user);
}
