package com.dg.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.dg.model.User;
import com.dg.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addCurrentUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("currentUser", currentUser);
        }
    }
}
