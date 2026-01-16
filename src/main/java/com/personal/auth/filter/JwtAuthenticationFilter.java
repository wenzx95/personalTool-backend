package com.personal.auth.filter;

import com.personal.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("JWT Filter - Processing request: {}", requestURI);

        // 从请求头中获取 token
        final String authHeader = request.getHeader("Authorization");
        final String tokenPrefix = "Bearer ";

        String username = null;
        String jwtToken = null;

        log.info("JWT Filter - Authorization header: {}", authHeader != null ? "present" : "missing");

        // 检查 Authorization 头
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            jwtToken = authHeader.substring(tokenPrefix.length());
            log.info("JWT Filter - Token extracted, length: {}", jwtToken.length());
            // 去除token中的所有非打印字符和无效字符
            jwtToken = jwtToken.replaceAll("\\p{Cntrl}", "").trim().replaceAll("\\s+", "");
            log.info("JWT Filter - Token cleaned, length: {}", jwtToken.length());
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
                log.info("JWT Filter - Username extracted: {}", username);
            } catch (Exception e) {
                log.error("JWT Token 解析失败: {}", e.getMessage());
            }
        }

        // 如果 token 有效，设置认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证 token 是否过期
                if (!jwtUtil.isTokenExpired(jwtToken)) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 设置到 Security Context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("JWT 认证成功: {}", username);
                } else {
                    log.warn("JWT Token 已过期: {}, 请求URI: {}", username, requestURI);
                    // Token过期时，清除SecurityContext，让Spring Security返回401而不是403
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                log.error("JWT 认证失败: {}, 请求URI: {}", e.getMessage(), requestURI);
                // 认证失败时，清除SecurityContext
                SecurityContextHolder.clearContext();
            }
        } else {
            if (username == null && authHeader != null && authHeader.startsWith(tokenPrefix)) {
                log.warn("JWT Filter - Token无效或无法解析，请求URI: {}", requestURI);
            } else {
                log.info("JWT Filter - Authentication already exists or username is null, auth: {}",
                    SecurityContextHolder.getContext().getAuthentication());
            }
        }

        filterChain.doFilter(request, response);
    }
}
