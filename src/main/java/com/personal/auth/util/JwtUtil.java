package com.personal.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author tendollar
 * @since 2026-01-11
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param nickname 昵称
     * @return token
     */
    public String generateToken(Long userId, String username, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("nickname", nickname);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从token中获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取昵称
     *
     * @param token token
     * @return 昵称
     */
    public String getNicknameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("nickname", String.class);
    }

    /**
     * 从token中获取创建时间
     *
     * @param token token
     * @return 创建时间
     */
    public Date getCreatedDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    /**
     * 从token中获取过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取Claims
     *
     * @param token token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        // 去除token中的所有非打印字符、无效字符和控制字符
        String cleanToken = token.replaceAll("[^\\x20-\\x7E]", "").trim().replaceAll("\\s+", "");
        System.out.println("Clean token: " + cleanToken);
        System.out.println("Token length after cleaning: " + cleanToken.length());
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(cleanToken)
                .getPayload();
    }

    /**
     * 获取签名密钥
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 检查token是否过期
     *
     * @param token token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    /**
     * 刷新token
     *
     * @param token 旧token
     * @return 新token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Jwts.builder()
                .claims(claims)
                .subject(claims.getSubject())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证token是否有效
     *
     * @param token token
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }
}
