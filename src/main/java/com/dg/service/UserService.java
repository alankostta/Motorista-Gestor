package com.dg.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dg.dto.UserRegistrationDTO;
import com.dg.model.Role;
import com.dg.model.User;
import com.dg.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(UserRegistrationDTO registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalStateException("Já existe uma conta com este email.");
        }
        
        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.ROLE_USER);
        
        return userRepository.save(user);
    }
    public User editarUser(UserRegistrationDTO userEditadoDTO) {
      
        
        User user = new User();
        user.setId(userEditadoDTO.getId());
        user.setFullName(userEditadoDTO.getFullName());
        user.setEmail(userEditadoDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userEditadoDTO.getPassword()));
        user.setRole(Role.ROLE_ADMIN);
        user.setCreatedDate(userEditadoDTO.getUpdatedDate());
        user.setUpdatedDate(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
