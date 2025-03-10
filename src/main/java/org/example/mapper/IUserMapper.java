package org.example.mapper;

import org.example.dto.user.UserAuthDto;
import org.example.entites.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    // Перетворення з DTO в Entity
    UserEntity userAuthDtoToUserEntity(UserAuthDto userAuthDto);

    // Перетворення з Entity в DTO
    UserAuthDto userEntityToUserAuthDto(UserEntity userEntity);
}
