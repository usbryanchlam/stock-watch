package dev.bryanlam.stockwatch.security;

import dev.bryanlam.stockwatch.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements OAuth2User, UserDetails {
    
    private String id;
    private String email;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(String id, String email, String name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }

    public static UserPrincipal create(UserDTO userDto) {
        Set<String> roles = new HashSet<>();
        roles.add(userDto.getRole());
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UserPrincipal(
            userDto.getId(),
            userDto.getEmail(),
            userDto.getName(),
                authorities
        );
    }

    public static UserPrincipal create(UserDTO userDto, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(userDto);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    // UserDetails interface methods
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 doesn't use password
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User interface methods
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }

    // Additional getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
