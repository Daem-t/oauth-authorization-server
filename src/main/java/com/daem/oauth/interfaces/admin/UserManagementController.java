package com.daem.oauth.interfaces.admin;

import com.daem.oauth.application.admin.UserManagementService;
import com.daem.oauth.domain.user.User;
import com.daem.oauth.interfaces.admin.dto.*;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.dto.MessageResponse;
import com.daem.oauth.interfaces.common.service.MessageService;
import com.daem.oauth.interfaces.common.util.LocaleUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * 用户管理控制器
 * 提供用户管理相关的REST API
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;
    private final MessageService messageService;

    public UserManagementController(UserManagementService userManagementService,
                                    MessageService messageService) {
        this.userManagementService = userManagementService;
        this.messageService = messageService;
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<UserListResponse> getUserList(
            @ModelAttribute UserListRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin getting user list: page={}, size={}, keyword={}",
                request.getPage(), request.getSize(), request.getKeyword());
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        UserListResponse response = userManagementService.getUserList(request);
        logger.info("Returned {} users to admin", response.getUsers().size());
        return ResponseEntity.ok(response);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<UserListResponse.UserInfo> createUser(
            @Valid @RequestBody UserCreateRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin creating new user: username={}", request.getUsername());
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        User newUser = userManagementService.createUser(request);
        UserListResponse.UserInfo userInfo = UserListResponse.UserInfo.fromDomainUser(newUser);
        logger.info("User created successfully by admin: userId={}", newUser.getId());

        // 返回 201 Created 状态，并在 Location 头中提供新资源的 URI
        URI location = URI.create(String.format("/api/admin/users/%d", newUser.getId()));
        return ResponseEntity.created(location).body(userInfo);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserListResponse.UserInfo> getUserById(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {

        logger.info("Admin getting user details: userId={}", userId);
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        User user = userManagementService.getUserById(userId);
        UserListResponse.UserInfo userInfo = UserListResponse.UserInfo.fromDomainUser(user);
        logger.info("Returned user details to admin: userId={}, username={}",
                userId, user.getUsername());
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserListResponse.UserInfo> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin updating user: userId={}, username={}", userId, request.getUsername());
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        User updatedUser = userManagementService.updateUser(userId, request);
        UserListResponse.UserInfo userInfo = UserListResponse.UserInfo.fromDomainUser(updatedUser);
        logger.info("User updated successfully by admin: userId={}", userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {

        logger.info("Admin deleting user: userId={}", userId);
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        userManagementService.deleteUser(userId);
        String message = messageService.getMessage("admin.users.delete.success");
        logger.info("User deleted successfully by admin: userId={}", userId);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<MessageResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin updating user status: userId={}, status={}", userId, request.getStatus());
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        userManagementService.updateUserStatus(userId, request);
        String message = messageService.getMessage("admin.users.status.update.success");
        logger.info("User status updated successfully by admin: userId={}, status={}",
                userId, request.getStatus());
        return ResponseEntity.ok(new MessageResponse(message));
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<MessageResponse> resetUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin resetting password for user: userId={}", userId);
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);
        userManagementService.resetUserPassword(userId, request);
        String message = messageService.getMessage("admin.users.password.reset.success");
        logger.info("Password reset successfully by admin: userId={}", userId);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch/status")
    public ResponseEntity<MessageResponse> batchUpdateUserStatus(
            @Valid @RequestBody BatchUserStatusUpdateRequest request,
            HttpServletRequest httpRequest) {

        logger.info("Admin batch updating user status: userIds={}, status={}",
                request.getUserIds(), request.getStatus());
        var userLocale = LocaleUtil.detectLocaleFromRequest(httpRequest);
        LocaleContextHolder.setLocale(userLocale);

        int updatedCount = 0;
        for (Long userId : request.getUserIds()) {
            try {
                UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest(
                        request.getStatus(), request.getReason());
                userManagementService.updateUserStatus(userId, statusRequest);
                updatedCount++;
            } catch (Exception e) {
                logger.warn("Failed to update user status in batch: userId={}, error={}",
                        userId, e.getMessage());
            }
        }

        String message = messageService.getMessage("admin.users.batch.update.success",
                new Object[]{updatedCount, request.getUserIds().size()});
        logger.info("Batch status update completed: updated={}, total={}",
                updatedCount, request.getUserIds().size());
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
