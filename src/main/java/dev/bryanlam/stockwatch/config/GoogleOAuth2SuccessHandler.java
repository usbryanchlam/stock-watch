package dev.bryanlam.stockwatch.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import dev.bryanlam.stockwatch.dto.UserDTO;
import dev.bryanlam.stockwatch.security.JwtTokenProvider;
import dev.bryanlam.stockwatch.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.Map;

@Component
public class GoogleOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private JwtTokenProvider tokenProvider;
    
    private UserServiceImpl userService;

    private static final String GOOGLE_PROVIDER = "google";
    
    @Autowired
    public GoogleOAuth2SuccessHandler (JwtTokenProvider tokenProvider, UserServiceImpl userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String pictureUrl = (String) attributes.get("picture");
            String googleId = (String) attributes.get("sub");

            UserDTO user = processUserRegistration(email, name, pictureUrl, googleId);
            Authentication userAuthentication = userService.createUserAuthentication(user, attributes);
            
            String token = tokenProvider.generateToken(userAuthentication);
            String redirectUrl = UriComponentsBuilder.fromUriString("/api/auth/oauth2-success")
                    .queryParam("token", token)
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
    
    private UserDTO processUserRegistration(String email, String name, String picture, String googleId) {
        UserDTO existingUser = userService.findByProviderAndProviderId(GOOGLE_PROVIDER, googleId);
        
        if (existingUser != null) {
            // Update existing user if necessary
            boolean needsUpdate = false;
            
            if (existingUser.getProviderId() == null || !existingUser.getProviderId().equals(googleId)) {
                existingUser.setProviderId(googleId);
                needsUpdate = true;
            }
            
            if (existingUser.getName() == null || !existingUser.getName().equals(name)) {
                existingUser.setName(name);
                needsUpdate = true;
            }
            
            if (existingUser.getPicture() == null || !existingUser.getPicture().equals(picture)) {
                existingUser.setPicture(picture);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                userService.save(existingUser);
            }
            
            return existingUser;
        }
        
        // Create new user
        UserDTO newUser = new UserDTO();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPicture(picture);
        newUser.setProvider(GOOGLE_PROVIDER);
        newUser.setProviderId(googleId);
        newUser.setRole("USER");
        
        return userService.save(newUser);
    }
}
