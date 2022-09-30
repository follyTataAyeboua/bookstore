package com.manulife.codingtest.bookstore.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.service.UserDetailsServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    Properties properties;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!StringUtils.contains(request.getServletPath(), "/api/auth")
                && !StringUtils.contains(request.getServletPath(), "/api/book/books")
                && !StringUtils.contains(request.getServletPath(), "/api/book/count")
                && !StringUtils.contains(request.getServletPath(), "swagger-ui")
                && !StringUtils.contains(request.getServletPath(), "/v3/api-docs")) {
            try {
                String jwt = properties.parseJwt(request);
                String requestUsername = properties.parseUsernameHeader(request);
                if (StringUtils.isNotEmpty(jwt) && jwtUtils.validateJwtToken(jwt)) {
                    String username = properties.getUseDefaultToken() ? requestUsername : jwtUtils.getUserNameFromJwtToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication:", e);
                response.setHeader("ERROR", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                final Map<String, Object> body = new HashMap<>();
                body.put("error", "Unauthorized");
                body.put("message", "Cannot set user authentication:" + e.getMessage());
                body.put("path", request.getServletPath());

                final ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), body);
            }

        }
        filterChain.doFilter(request, response);
    }

}
