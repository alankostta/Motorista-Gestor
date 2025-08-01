package com.dg.dto;

import java.time.LocalDateTime;
import com.dg.model.Role;
import com.dg.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
	
	
	private Long id;
  
	private Role role = Role.ROLE_USER;
	     
	private LocalDateTime createdDate;
	    
	private LocalDateTime updatedDate;
    
    @NotBlank(message = "O nome completo é obrigatório")
    private String fullName;
    
    @Email(message = "Email deve ter um formato válido")
    @NotBlank(message = "O email é obrigatório")
    private String email;
    
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    @NotBlank(message = "A senha é obrigatória")
    private String password;

    // Constructors
    public UserRegistrationDTO() {}

    public UserRegistrationDTO(Long id,Role role, String fullName, String email, String password, LocalDateTime createdDate, LocalDateTime updatedDate) {
    	this.id = id;
    	this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public UserRegistrationDTO toUserDTO (User user) {
	
	UserRegistrationDTO userDto = new UserRegistrationDTO();
	this.setId(user.getId());
	this.setFullName(user.getFullName());
	this.setEmail(user.getEmail());
	this.setPassword(user.getPassword());
	this.setRole(user.getRole());
	this.setCreatedDate(user.getCreatedDate());
	this.setUpdatedDate(user.getUpdatedDate());
	
	return userDto;
    }
}

