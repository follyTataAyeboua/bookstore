package com.manulife.codingtest.bookstore.security.controller;

import com.google.gson.Gson;
import com.manulife.codingtest.bookstore.JsonUtil;
import com.manulife.codingtest.bookstore.config.payload.MessageResponse;
import com.manulife.codingtest.bookstore.config.payload.ResponseType;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.payload.request.LoginRequest;
import com.manulife.codingtest.bookstore.security.payload.request.SignUpRequest;
import com.manulife.codingtest.bookstore.security.payload.response.JwtResponse;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService service;

    @MockBean
    private Properties properties;

    @Before
    public void setUp() {
    }

    public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return new Gson().fromJson(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whenUserSignupAndUsernameIsIncorrect_thenCreateAuthor() throws Exception {
        String success = "User registered successfully!";

        MessageResponse response = new MessageResponse(ResponseType.SUCCESS, success);

        SignUpRequest wrongUsername = new SignUpRequest();
        wrongUsername.setEmail("testwell@bookstore.com");
        wrongUsername.setFirstname("Te");
        wrongUsername.setLastname("Test");
        wrongUsername.setUsername("Test");
        wrongUsername.setPassword("Test1231A!");

        given(service.registerUser(Mockito.any())).willReturn(response);

        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(wrongUsername)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].message", is("firstname size must be between 3 and 20")));

        verify(service, VerificationModeFactory.times(0)).authenticateUser(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).registerUser(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).validateUsername(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).validateEmail(Mockito.any());

        reset(service);
    }

    @Test
    public void whenUserSignup_thenCreateAuthor() throws Exception {
        String success = "User registered successfully!";
        String email = "test@test.com";
        String password = "Test1A!";

        MessageResponse response = new MessageResponse(ResponseType.SUCCESS, success);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setFirstname("Test");
        signUpRequest.setLastname("Test");
        signUpRequest.setUsername("Test");
        signUpRequest.setPassword(password);

        given(service.registerUser(Mockito.any())).willReturn(response);

        MvcResult requestResult = mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn();

        MessageResponse mvcResponse = parseResponse(requestResult, MessageResponse.class);
        assertEquals(response.getResponseType(), mvcResponse.getResponseType());
        assertEquals(response.getMessage(), mvcResponse.getMessage());

        verify(service, VerificationModeFactory.times(0)).authenticateUser(Mockito.any());
        verify(service, VerificationModeFactory.times(1)).registerUser(Mockito.any());
        verify(service, VerificationModeFactory.times(1)).validateUsername(Mockito.any());
        verify(service, VerificationModeFactory.times(1)).validateEmail(Mockito.any());

        reset(service);
    }


    @Test
    public void whenUserLogin_thenCreateToken() throws Exception {
        JwtResponse response = new JwtResponse("bookStore2022", "bookStore2022", "test", "test@test.com", Collections.EMPTY_LIST);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("Test");
        loginRequest.setPassword("Test1A!");

        given(service.authenticateUser(Mockito.any())).willReturn(response);

        MvcResult requestResult = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse mvcResponse = parseResponse(requestResult, JwtResponse.class);
        assertEquals(response.getUsername(), mvcResponse.getUsername());
        assertEquals(response.getEmail(), mvcResponse.getEmail());
        assertEquals(response.getAccessToken(), mvcResponse.getAccessToken());
        assertEquals(response.getRefreshToken(), mvcResponse.getRefreshToken());

        verify(service, VerificationModeFactory.times(1)).authenticateUser(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).registerUser(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).validateUsername(Mockito.any());
        verify(service, VerificationModeFactory.times(0)).validateEmail(Mockito.any());

        reset(service);
    }
}
