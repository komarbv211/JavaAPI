package org.example.mapper;

import org.example.dto.user.RoleDto;
import org.example.dto.user.UserItemDto;
import org.example.entites.UserEntity;
import org.example.entites.UserRoleEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = IRoleMapper.class) // Використовуємо IRoleMapper для перетворення ролей
public interface IUserMapper {
    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    @Mapping(source = "userRoles", target = "roles", qualifiedByName = "mapRoles")
    UserItemDto toDto(UserEntity user);

    @Named("mapRoles")
    default List<RoleDto> mapRoles(Set<UserRoleEntity> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) return List.of();
        return userRoles.stream()
                .map(userRole -> IRoleMapper.INSTANCE.toDto(userRole.getRole())) // Використовуємо IRoleMapper для перетворення ролі
                .collect(Collectors.toList());
    }

    List<UserItemDto> toDto(List<UserEntity> users);
}
