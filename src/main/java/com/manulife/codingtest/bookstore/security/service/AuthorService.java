package com.manulife.codingtest.bookstore.security.service;

import com.manulife.codingtest.bookstore.config.payload.MessageResponse;
import com.manulife.codingtest.bookstore.config.payload.ResponseType;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.config.JwtUtils;
import com.manulife.codingtest.bookstore.security.config.TokenType;
import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.domain.Role;
import com.manulife.codingtest.bookstore.security.domain.RoleType;
import com.manulife.codingtest.bookstore.security.mapper.AuthorMapper;
import com.manulife.codingtest.bookstore.security.payload.request.AuthorDto;
import com.manulife.codingtest.bookstore.security.payload.request.LoginRequest;
import com.manulife.codingtest.bookstore.security.payload.request.SignUpRequest;
import com.manulife.codingtest.bookstore.security.payload.response.JwtResponse;
import com.manulife.codingtest.bookstore.security.repository.AuthorRepository;
import com.manulife.codingtest.bookstore.security.repository.RoleRepository;
import com.manulife.codingtest.bookstore.security.utils.Constants;
import com.manulife.codingtest.bookstore.store.controller.BookController;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    Properties properties;

    private final AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final String ADMIN = "ADMIN";

    public Author getAuthor(String username) {
        logger.info("getAuthor user {}", username);
        return authorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Not Author found for provided username: " + username));
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(userDetails, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtUtils.generateJwtToken(userDetails, TokenType.REFRESH_TOKEN);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(accessToken, refreshToken, userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    public MessageResponse registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("Register user {}", signUpRequest.getUsername());
        Author author = new Author(signUpRequest.getUsername(), signUpRequest.getFirstname(), signUpRequest.getLastname(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_AUTHOR)
                    .orElseThrow(() -> new RuntimeException("No Role found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (ADMIN.equalsIgnoreCase(role)) {
                    Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("No Role found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(RoleType.ROLE_AUTHOR)
                            .orElseThrow(() -> new RuntimeException("No Role found."));
                    roles.add(userRole);
                }
            });
        }

        author.setRoles(roles);
        authorRepository.save(author);

        return new MessageResponse(ResponseType.SUCCESS, "User registered successfully!");
    }

    public boolean validateUsername(SignUpRequest signUpRequest) {
        return authorRepository.existsByUsername(signUpRequest.getUsername());
    }

    public boolean validateEmail(SignUpRequest signUpRequest) {
        return authorRepository.existsByEmail(signUpRequest.getEmail());
    }

    public JwtResponse refreshToken(String headerAuth, String requestUsername) {
        try {
            if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(Constants.BEARER)) {
                String jwt = headerAuth.substring(7, headerAuth.length());
                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = properties.getUseDefaultToken() ? requestUsername : jwtUtils.getUserNameFromJwtToken(jwt);

                    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    String accessToken = jwtUtils.generateJwtToken(userDetails, TokenType.ACCESS_TOKEN);

                    return new JwtResponse(accessToken, jwt, userDetails.getUsername(), userDetails.getEmail(), roles);
                } else {
                    throw new Exception("Refresh token expired or not valid");
                }
            } else {
                throw new Exception("Refresh token missing");
            }
        } catch (Exception e) {
            logger.error("Cannot refresh user token", e);
            throw new RuntimeException("Cannot refresh user token: " + e.getMessage());
        }
    }

    public String getUsernameFromHeader(String headerAuth, String requestUsername) {

        try {
            if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(Constants.BEARER)) {
                String jwt = headerAuth.substring(7, headerAuth.length());
                if (jwtUtils.validateJwtToken(jwt)) {
                    return properties.getUseDefaultToken() ? requestUsername : jwtUtils.getUserNameFromJwtToken(jwt);
                } else {
                    throw new Exception("Refresh token expired or not valid");
                }
            } else {
                throw new Exception("Refresh token missing");
            }
        } catch (Exception e) {
            logger.error("Cannot refresh user token", e);
            throw new RuntimeException("Cannot get user from token : " + e.getMessage());
        }
    }

    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream().map(authorMapper::authorToAuthorDto).collect(Collectors.toList());
    }

}

