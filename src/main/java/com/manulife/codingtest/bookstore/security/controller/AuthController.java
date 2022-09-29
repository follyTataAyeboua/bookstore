package com.manulife.codingtest.bookstore.security.controller;

import com.manulife.codingtest.bookstore.config.payload.MessageResponse;
import com.manulife.codingtest.bookstore.config.payload.ResponseType;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.payload.request.LoginRequest;
import com.manulife.codingtest.bookstore.security.payload.request.SignUpRequest;
import com.manulife.codingtest.bookstore.security.payload.response.JwtResponse;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthorService authorService;

    @Autowired
    Properties properties;

    @Operation(summary = "User login")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authorService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "User signup")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest authorDto) {
        try {
            if (authorService.validateUsername(authorDto)) {
                return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, "Username is already taken!"));
            }
            if (authorService.validateEmail(authorDto)) {
                return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, "Email is already taken!"));
            }
            return ResponseEntity.ok(authorService.registerUser(authorDto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Refresh token")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String headerAuth = properties.getAuthorizationHeader(request);
            String requestUsername = properties.parseUsernameHeader(request);
            JwtResponse jwtResponse = authorService.refreshToken(headerAuth, requestUsername);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }
}
