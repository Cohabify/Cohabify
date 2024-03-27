package org.ispp4.cohabify.authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.ispp4.cohabify.user.Gender;
import org.ispp4.cohabify.user.Plan;
import org.ispp4.cohabify.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;


@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private String validToken;
    private String invalidToken;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Generate a valid token for testing
        List<String> authorities = new ArrayList<String>();
        authorities.add("User");
        user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setIsOwner(false);
        user.setPhone("666777666");
        user.setEmail("test@gmail.com");
        user.setDescription("descripcion test");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setAuthorities(authorities);
        user.setIsVerified(true);
        jwtService.jwtExpirationMs = 1000000L;
        validToken = jwtService.generateToken(user);

        // Generate an invalid token for testing
        invalidToken = validToken + "invalid";
        userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        jwtService.jwtExpirationMs = 1000000L;
        validToken = null;
        invalidToken = null;
        user = null;
        userDetails = null;
    }

    @Test
    void shouldExtractUserNameFromValidToken() {
        String extractedUserName = jwtService.extractUserName(validToken);
        assertEquals("test", extractedUserName);
    }

    @Test
    void shouldNotExtractUserNameFromInvalidToken() {
        assertThrows(SignatureException.class, () -> jwtService.extractUserName(invalidToken));
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(user);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldValidateToken() {
        assertTrue(jwtService.isTokenValid(validToken, userDetails));
    }

    @Test
    void shouldNotValidateToken() {
        assertThrows(SignatureException.class, () -> jwtService.isTokenValid(invalidToken, userDetails));
    }

    @Test
    void shouldNotValidateTokenWithDifferentUser() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("differentUser", user.getPassword(), new ArrayList<>());
        assertFalse(jwtService.isTokenValid(validToken, userDetails));
    }

    @Test
    void shouldNotValidateExpiredToken() {
        // Generate a token with a short expiration time
        jwtService.jwtExpirationMs = 1L;
        String token = jwtService.generateToken(user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

}
