package com.homesolutions;

import com.homesolutions.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private long testExpiration;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        testExpiration = 86400000L; // 24 hours in milliseconds

        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        userDetails = User.builder()
                .username("1234567890")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("1234567890");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("1234567890");

        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("1234567890");
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken("1234567890");

        Date expiration = jwtUtil.extractExpiration(token);

        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken("1234567890");

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtUtil.generateToken("1234567890");

        UserDetails differentUser = User.builder()
                .username("9876543210")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        Boolean isValid = jwtUtil.validateToken(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", 1L); // 1 millisecond

        String token = shortExpirationJwtUtil.generateToken("1234567890");

        Thread.sleep(100); // Wait for token to expire

        // The validateToken method will throw an exception when extracting username from expired token
        // We need to verify it returns false or throws an exception
        try {
            Boolean isValid = shortExpirationJwtUtil.validateToken(token, userDetails);
            assertThat(isValid).isFalse();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Expected exception for expired token
            assertThat(e).isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
        }
    }

    @Test
    void testExtractClaim() {
        String token = jwtUtil.generateToken("1234567890");

        String subject = jwtUtil.extractClaim(token, Claims::getSubject);

        assertThat(subject).isEqualTo("1234567890");
    }

    @Test
    void testGenerateToken_DifferentUsers() {
        String token1 = jwtUtil.generateToken("1234567890");
        String token2 = jwtUtil.generateToken("9876543210");

        assertThat(token1).isNotEqualTo(token2);

        String username1 = jwtUtil.extractUsername(token1);
        String username2 = jwtUtil.extractUsername(token2);

        assertThat(username1).isEqualTo("1234567890");
        assertThat(username2).isEqualTo("9876543210");
    }

    @Test
    void testTokenIssuedAtBeforeExpiration() {
        String token = jwtUtil.generateToken("1234567890");

        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertThat(issuedAt).isNotNull();
        assertThat(expiration).isNotNull();
        assertThat(issuedAt).isBefore(expiration);
        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(testExpiration);
    }

    @Test
    void testValidateToken_WithCorrectUsernameAndNotExpired() {
        String token = jwtUtil.generateToken("1234567890");

        UserDetails matchingUser = User.builder()
                .username("1234567890")
                .password("anyPassword")
                .authorities(Collections.emptyList())
                .build();

        Boolean isValid = jwtUtil.validateToken(token, matchingUser);

        assertThat(isValid).isTrue();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(testSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
