package com.daem.oauth.infrastructure.user;

import com.daem.oauth.domain.user.Role;

public class RoleMapper {
    public static Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static RoleEntity toEntity(Role role) {
        if (role == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        return entity;
    }
}
