package com.khutircraftubackend.mapper;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Інтерфейс UserMapper мапить дані між моделлю User та DTO UserDTO.
 */

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User userDTOToUser(UserDTO userDTO);
    UserDTO userToUserDTO(User user);
}
