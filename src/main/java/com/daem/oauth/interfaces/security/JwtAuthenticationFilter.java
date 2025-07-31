package com.daem.oauth.interfaces.security;

import com.daem.oauth.application.security.JwtTokenService;
import com.daem.oauth.domain.user.User;
import com.daem.oauth.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && jwtTokenService.validateToken(token)) {
            String username = jwtTokenService.getUsernameFromToken(token);
            
            // 从数据库获取用户信息（可以考虑缓存优化）
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null && "ACTIVE".equals(user.getStatus())) {
                // 创建认证对象
                List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toList());
                
                // 添加角色权限
                user.getRoles().forEach(role -> 
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
                );
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中提取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 跳过不需要认证的路径
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过认证相关的端点
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/captcha") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login") ||
               path.startsWith("/error") ||
               path.startsWith("/dev/") ||
               path.startsWith("/h2-console") ||
               path.equals("/");
    }
}