package dev.bryanlam.stockwatch.service;

import dev.bryanlam.stockwatch.dto.UserDTO;

public interface UserService {
    
    public UserDTO findByProviderAndProviderId(String provider, String providerId);

    public UserDTO save(UserDTO userDto);

    public void delete(UserDTO userDto);

}
