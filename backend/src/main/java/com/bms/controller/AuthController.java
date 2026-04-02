package com.bms.controller;

import com.bms.dto.AuthRequest;
import com.bms.dto.AuthResponse;
import com.bms.dto.RegisterRequest;
import com.bms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean cookieSecure;

    public AuthController(AuthService authService,
                          @Value("${server.servlet.session.cookie.secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse httpResponse) {
        AuthResponse response = authService.register(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        addRefreshTokenCookie(httpResponse, response.getRefreshToken(), 7 * 24 * 60 * 60);
        response.setRefreshToken(null); // Don't expose in body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request, HttpServletResponse httpResponse) {
        AuthResponse response = authService.authenticate(request);
        
        addRefreshTokenCookie(httpResponse, response.getRefreshToken(), 7 * 24 * 60 * 60);
        response.setRefreshToken(null); // Don't expose in body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(jakarta.servlet.http.HttpServletRequest request) {
        String refreshTokenStr = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenStr = cookie.getValue();
                }
            }
        }
        
        if (refreshTokenStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        var refreshTokenOpt = authService.getRefreshToken(refreshTokenStr);
        if (refreshTokenOpt.isPresent()) {
            return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse httpResponse) {
        addRefreshTokenCookie(httpResponse, "", 0); // Clear cookie
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody com.bms.dto.ResetPasswordRequest request) {
        boolean success = authService.resetPassword(request);
        if (success) {
            return ResponseEntity.ok("Password reset successfully. You can now login.");
        }
        return ResponseEntity.badRequest().body("Security question answer is incorrect or user not found.");
    }

    /** Centralized cookie builder — reads secure flag from environment config */
    private void addRefreshTokenCookie(HttpServletResponse httpResponse, String tokenValue, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenValue)
                .httpOnly(true)
                .secure(cookieSecure) // Reads from server.servlet.session.cookie.secure property
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
