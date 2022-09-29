package com.manulife.codingtest.bookstore.security.config;

import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    Properties properties;

    public String generateJwtToken(UserDetailsImpl userPrincipal, TokenType tokenType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("Roles", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        if (tokenType == TokenType.ACCESS_TOKEN) {
            if (properties.getUseDefaultToken()) {
                return properties.getDefaultToken();
            } else {
                return Jwts.builder()
                        .setSubject((userPrincipal.getUsername()))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date((new Date()).getTime() + properties.getAccessTokenExpiration() * 60 * 1000))
                        .addClaims(claims)
                        .signWith(SignatureAlgorithm.HS512, properties.getJwtSecret())
                        .compact();
            }
        } else {
            return Jwts.builder()
                    .setSubject((userPrincipal.getUsername()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + properties.getRefreshTokenExpiration() * 60 * 1000))
                    .signWith(SignatureAlgorithm.HS512, properties.getJwtSecret())
                    .compact();
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(properties.getJwtSecret()).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            if (properties.getUseDefaultToken()) {
                return StringUtils.equals(authToken, properties.getDefaultToken());
            }
            Jwts.parser().setSigningKey(properties.getJwtSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
