package com.daem.oauth.interfaces.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户列表响应DTO
 */
public class UserListResponse {
    
    private List<UserInfo> users;
    private PageInfo pagination;
    
    public UserListResponse() {}
    
    public UserListResponse(List<UserInfo> users, PageInfo pagination) {
        this.users = users;
        this.pagination = pagination;
    }
    
    /**
     * 从Spring Data JPA的Page对象创建响应
     */
    public static UserListResponse fromPage(Page<com.daem.oauth.domain.user.User> userPage) {
        List<UserInfo> users = userPage.getContent().stream()
                .map(UserInfo::fromDomainUser)
                .collect(Collectors.toList());
        
        PageInfo pageInfo = PageInfo.fromPage(userPage);
        
        return new UserListResponse(users, pageInfo);
    }
    
    // Getters and Setters
    public List<UserInfo> getUsers() { return users; }
    public void setUsers(List<UserInfo> users) { this.users = users; }
    
    public PageInfo getPagination() { return pagination; }
    public void setPagination(PageInfo pagination) { this.pagination = pagination; }
    
    /**
     * 用户信息
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String status;
        private Boolean enabled;
        private List<String> roles;
        
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        
        @JsonProperty("last_login_at")
        private LocalDateTime lastLoginAt;
        
        @JsonProperty("login_count")
        private Integer loginCount = 0;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String status, Boolean enabled, 
                       List<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.status = status;
            this.enabled = enabled;
            this.roles = roles;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        /**
         * 从领域用户对象创建DTO
         */
        public static UserInfo fromDomainUser(com.daem.oauth.domain.user.User user) {
            List<String> roleNames = user.getRoles() != null ? 
                    user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toList()) : 
                    List.of();
            
            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getStatus(),
                    user.getEnabled(),
                    roleNames,
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
            
            // 设置登录相关信息（如果有的话）
            userInfo.setLastLoginAt(user.getLastLoginAt());
            userInfo.setLoginCount(user.getLoginCount() != null ? user.getLoginCount() : 0);
            
            return userInfo;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
        
        public Integer getLoginCount() { return loginCount; }
        public void setLoginCount(Integer loginCount) { this.loginCount = loginCount; }
    }
    
    /**
     * 分页信息
     */
    public static class PageInfo {
        private Integer page;
        private Integer size;
        private Long total;
        
        @JsonProperty("total_pages")
        private Integer totalPages;
        
        @JsonProperty("has_next")
        private Boolean hasNext;
        
        @JsonProperty("has_previous")
        private Boolean hasPrevious;
        
        @JsonProperty("is_first")
        private Boolean isFirst;
        
        @JsonProperty("is_last")
        private Boolean isLast;
        
        public PageInfo() {}
        
        public PageInfo(Integer page, Integer size, Long total, Integer totalPages) {
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = totalPages;
            this.hasNext = page < totalPages - 1;
            this.hasPrevious = page > 0;
            this.isFirst = page == 0;
            this.isLast = page == totalPages - 1;
        }
        
        /**
         * 从Spring Data JPA的Page对象创建分页信息
         */
        public static PageInfo fromPage(Page<?> page) {
            PageInfo pageInfo = new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
            
            pageInfo.setHasNext(page.hasNext());
            pageInfo.setHasPrevious(page.hasPrevious());
            pageInfo.setIsFirst(page.isFirst());
            pageInfo.setIsLast(page.isLast());
            
            return pageInfo;
        }
        
        // Getters and Setters
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
        
        public Boolean getHasNext() { return hasNext; }
        public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }
        
        public Boolean getHasPrevious() { return hasPrevious; }
        public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }
        
        public Boolean getIsFirst() { return isFirst; }
        public void setIsFirst(Boolean isFirst) { this.isFirst = isFirst; }
        
        public Boolean getIsLast() { return isLast; }
        public void setIsLast(Boolean isLast) { this.isLast = isLast; }
    }
}