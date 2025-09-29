package com.dg.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
        user.setProfilePhoto(userEditadoDTO.getProfilePhoto());
        user.setRole(Role.ROLE_ADMIN);
        user.setCreatedDate(userEditadoDTO.getUpdatedDate());
        user.setUpdatedDate(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    public String saveProfilePhoto(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        
        // Criar diretório se não existir - usando caminho absoluto
        String uploadDir = "src/main/resources/static/uploads/profile-photos/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Gerar nome único para o arquivo
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = "user_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
        
        // Salvar arquivo
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }
    
    public void deleteProfilePhoto(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path filePath = Paths.get("src/main/resources/static/uploads/profile-photos/" + fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Erro ao deletar foto: " + e.getMessage());
            }
        }
    }
}
