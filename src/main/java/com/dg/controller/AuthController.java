package com.dg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dg.dto.UserRegistrationDTO;
import com.dg.model.User;
import com.dg.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
    	model.addAttribute("activePage", "login");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "user-register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDTO userDto, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user-register";
        }
        
        try {
            userService.registerNewUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Registro realizado com sucesso! Faça o login.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user-register";
        }
    }
    @GetMapping("/userEdite")  
    public String getAnalytics(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    	
    	User currentUser = userService.findByEmail(userDetails.getUsername());

    	UserRegistrationDTO userDto = new UserRegistrationDTO();
    	userDto.toUserDTO(currentUser);
        model.addAttribute("userDto", userDto);
          
        return "user-register";
    }
    
    @PostMapping("/salvarEdite")
    public String salvarRegisterUser(@Valid @ModelAttribute("userDto") UserRegistrationDTO userDto, 
                             BindingResult result, 
                             Model model,
                             @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user-register";
        }
        
        try {
            // Se uma nova foto foi enviada, salvar
            if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
                User currentUser = userService.findByEmail(userDetails.getUsername());
                
                // Deletar foto antiga se existir
                if (currentUser.getProfilePhoto() != null) {
                    userService.deleteProfilePhoto(currentUser.getProfilePhoto());
                }
                
                // Salvar nova foto
                String fileName = userService.saveProfilePhoto(profilePhotoFile, userDto.getId());
                userDto.setProfilePhoto(fileName);
            } else {
                // Manter foto atual se nenhuma nova foi enviada
                User currentUser = userService.findByEmail(userDetails.getUsername());
                userDto.setProfilePhoto(currentUser.getProfilePhoto());
            }
            
            userService.editarUser(userDto);
            
            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado com sucesso!");
            return "redirect:/userEdite";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao salvar perfil: " + e.getMessage());
            return "user-register";
        }
    }
        
}
