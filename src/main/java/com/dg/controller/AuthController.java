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
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDTO userDto, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            userService.registerNewUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Registro realizado com sucesso! Faça o login.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
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
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        userService.editarUser(userDto);
        return "redirect:/dashboard";
    }
        
}
