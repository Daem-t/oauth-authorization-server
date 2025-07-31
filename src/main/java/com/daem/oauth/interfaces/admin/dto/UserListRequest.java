package com.daem.oauth.interfaces.admin.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * 用户列表查询请求DTO
 */
public class UserListRequest {
    
    private Integer page = 0;
    private Integer size = 20;
    private String keyword;
    private String status;
    private String role;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    
    public UserListRequest() {}
    
    /**
     * 转换为Spring Data JPA的Pageable对象
     */
    public Pageable toPageable() {
        // 验证和设置默认值
        int pageNumber = Math.max(0, this.page != null ? this.page : 0);
        int pageSize = Math.min(100, Math.max(1, this.size != null ? this.size : 20));
        
        // 创建排序对象
        Sort sort = createSort();
        
        return PageRequest.of(pageNumber, pageSize, sort);
    }
    
    /**
     * 创建排序对象
     */
    private Sort createSort() {
        String sortField = StringUtils.hasText(this.sortBy) ? this.sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(this.sortDirection) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        // 验证排序字段，防止SQL注入
        if (!isValidSortField(sortField)) {
            sortField = "createdAt";
        }
        
        return Sort.by(direction, sortField);
    }
    
    /**
     * 验证排序字段是否合法
     */
    private boolean isValidSortField(String field) {
        return field.matches("^[a-zA-Z][a-zA-Z0-9_]*$") && 
               (field.equals("id") || field.equals("username") || field.equals("email") || 
                field.equals("status") || field.equals("createdAt") || field.equals("updatedAt") ||
                field.equals("lastLoginAt") || field.equals("loginCount"));
    }
    
    /**
     * 检查是否有搜索条件
     */
    public boolean hasSearchCriteria() {
        return StringUtils.hasText(keyword) || 
               StringUtils.hasText(status) || 
               StringUtils.hasText(role);
    }
    
    // Getters and Setters
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}