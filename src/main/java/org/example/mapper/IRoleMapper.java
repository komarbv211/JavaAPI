package org.example.mapper;

import org.example.dto.user.RoleDto;
import org.example.entites.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
    IRoleMapper INSTANCE = Mappers.getMapper(IRoleMapper.class);

    RoleDto toDto(RoleEntity roleEntity);
}
