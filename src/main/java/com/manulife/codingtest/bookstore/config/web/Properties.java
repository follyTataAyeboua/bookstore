package com.manulife.codingtest.bookstore.config.web;

import com.manulife.codingtest.bookstore.security.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class Properties {

    @Value("${bookStore.app.token.default}")
    private String defaultToken;

    @Value("${bookStore.app.token.useDefault}")
    private Boolean useDefaultToken;

    @Value("${bookStore.app.header.username}")
    private String usernameHeader;

    @Value("${bookStore.app.header.authorization}")
    private String authorizationHeader;

    @Value("${bookStore.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bookStore.app.accessToken.expiration}")
    private Long accessTokenExpiration;

    @Value("${bookStore.app.refreshToken.expiration}")
    private Long refreshTokenExpiration;

    public String getDefaultToken() {
        return defaultToken;
    }

    public void setDefaultToken(String defaultToken) {
        this.defaultToken = defaultToken;
    }

    public Boolean getUseDefaultToken() {
        return useDefaultToken;
    }

    public void setUseDefaultToken(Boolean useDefaultToken) {
        this.useDefaultToken = useDefaultToken;
    }

    public String getUsernameHeader() {
        return usernameHeader;
    }

    public void setUsernameHeader(String usernameHeader) {
        this.usernameHeader = usernameHeader;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }


    public String parseJwt(HttpServletRequest request) {
        String headerAuth = getAuthorizationHeader(request);

        if (StringUtils.isNotEmpty(headerAuth) && headerAuth.startsWith(Constants.BEARER)) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    public String parseUsernameHeader(HttpServletRequest request) {
        String headerUsername = request.getHeader(usernameHeader);
        return StringUtils.isNotEmpty(headerUsername) ? headerUsername : null;
    }

    public String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(authorizationHeader);
    }
}
