package dev.bryanlam.stockwatch.service.impl;

import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.bryanlam.stockwatch.dto.UserDTO;
import dev.bryanlam.stockwatch.model.User;
import dev.bryanlam.stockwatch.repository.UserRepository;
import dev.bryanlam.stockwatch.service.UserService;

import dev.bryanlam.stockwatch.security.UserPrincipal;
import dev.bryanlam.stockwatch.exception.ResourceNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService  {

    private UserRepository userRepository;

    private static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO findByProviderAndProviderId(String provider, String providerId) {
        Optional<User> user = userRepository.findByProviderAndProviderId(provider, providerId);
        
        if (user.isPresent())
            return getUserDTO(user.get());
        else
            return null;
    }

    @Override
    public UserDTO save(UserDTO userDto) {
        User user = userRepository.save(getUserEntity(userDto));

        return getUserDTO(user);
    }

    @Override
    public void delete(UserDTO userDto) {
        userRepository.deleteById(userDto.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with email : " + email)
                );

        return UserPrincipal.create(getUserDTO(user));
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> 
                    new ResourceNotFoundException("User", "id", id)
                );

        return UserPrincipal.create(getUserDTO(user));
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return Optional.of(getUserDTO(user.get()));
        }
        else {
            return Optional.of(null);
        }
        
    }

    public Authentication createUserAuthentication(UserDTO userDto, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(userDto, attributes);
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }
    
    public UserDTO getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        return getUserDTO(user); 
    }

    private UserDTO getUserDTO(User user) {
        return (modelMapper.map(user, UserDTO.class));
    }

    private User getUserEntity(UserDTO userDto) {
        return (modelMapper.map(userDto, User.class));
    }
}
