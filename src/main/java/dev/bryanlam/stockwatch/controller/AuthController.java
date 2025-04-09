package dev.bryanlam.stockwatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import dev.bryanlam.stockwatch.dto.ApiResponse;
import dev.bryanlam.stockwatch.security.CsrfTokenProvider;
import dev.bryanlam.stockwatch.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CsrfTokenProvider csrfProvider;

    @Value("${app.client.callbackuri}")
    private String clientCallbackURI;

    @GetMapping("/oauth2-success")
    public ResponseEntity<Void> handleOAuth2Success(
            @RequestParam String token) {
        // This endpoint serves as a redirect target after OAuth2 authentication
        String redirectUrl = UriComponentsBuilder.fromUriString(clientCallbackURI).toUriString();

        // Create JWT cookie
        ResponseCookie jwtCookie = tokenProvider.createJWTCookie(token);

        // Create CSRF cookie
        ResponseCookie csrfCookie = csrfProvider.createCsrfTokenAndCookie();
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, csrfCookie.toString());

        headers.add(HttpHeaders.LOCATION, redirectUrl);
        
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signout() {
        ResponseCookie jwtCookie = tokenProvider.invalidateJWTCookie();
        ResponseCookie csrfCookie = csrfProvider.invalidateCsrfTokenCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, csrfCookie.toString());


        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(null, "Signed out successfully"));
    }
}
