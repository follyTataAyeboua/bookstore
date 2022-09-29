package com.manulife.codingtest.bookstore.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    Properties properties;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = properties.parseJwt(request);
            String requestUsername = properties.parseUsernameHeader(request);
            if (StringUtils.hasText(jwt) && jwtUtils.validateJwtToken(jwt)) {
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
//            log.error("Cannot set user authentication:", e);
            response.setHeader("ERROR", e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            final Map<String, Object> body = new HashMap<>();
            body.put("error", "Unauthorized");
            body.put("message", "Cannot set user authentication:" + e.getMessage());
            body.put("path", request.getServletPath());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        }

        filterChain.doFilter(request, response);
    }


}
