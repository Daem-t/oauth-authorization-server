package com.daem.oauth.infrastructure.user;

import com.daem.oauth.domain.user.Role;
import com.daem.oauth.domain.user.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        Set<Role> roles = null;
        if (entity.getRoles() != null) {
            roles = entity.getRoles().stream()
                    .map(RoleMapper::toDomain)
                    .collect(Collectors.toSet());
        }
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getActivationToken(),
                entity.getActivationTokenExpiry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                null, // lastLoginAt is not in UserEntity
                0,    // loginCount is not in UserEntity
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired(),
                entity.getEnabled(),
                roles
        );
    }

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEmail(user.getEmail());
        entity.setStatus(user.getStatus());
        entity.setActivationToken(user.getActivationToken());
        entity.setActivationTokenExpiry(user.getActivationTokenExpiry());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setAccountNonExpired(user.getAccountNonExpired());
        entity.setAccountNonLocked(user.getAccountNonLocked());
        entity.setCredentialsNonExpired(user.getCredentialsNonExpired());
        entity.setEnabled(user.getEnabled());
        if (user.getRoles() != null) {
            Set<RoleEntity> roleEntities = user.getRoles().stream()
                    .map(role -> {
                        RoleEntity roleEntity = new RoleEntity();
                        roleEntity.setId(role.getId());
                        roleEntity.setName(role.getName());
                        roleEntity.setDescription(role.getDescription());
                        return roleEntity;
                    })
                    .collect(Collectors.toSet());
            entity.setRoles(roleEntities);
        }
        return entity;
    }

}
 