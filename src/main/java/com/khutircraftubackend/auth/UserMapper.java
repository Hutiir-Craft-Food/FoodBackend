package com.khutircraftubackend.auth;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Інтерфейс UserMapper мапить дані між моделлю User та DTO UserDTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * The constant INSTANCE.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * User dto to user user.
     *
     * @param userDTO the user dto
     * @return the user
     */
    User userDTOToUser(UserDTO userDTO);

    /**
     * User to user dto user dto.
     *
     * @param user the user
     * @return the user dto
     */
    UserDTO userToUserDTO(User user);
}
