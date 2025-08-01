package com.XzaoDoMal.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.XzaoDoMal.dto.LoginRequest;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @NotBlank(message = "Login é obrigatório")
    @Size(min = 3, max = 50, message = "Login deve ter entre 3 e 50 caracteres")
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @Column(name = "senha", length = 300, nullable = false)
    private String senha;
    
    @Transient
    @NotBlank(message = "A confirmação da senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String confirmaSenha;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
    		name="tb_users_roles",
    		joinColumns = @JoinColumn(name="user_id"),
    		inverseJoinColumns = @JoinColumn(name="role_id")
    		)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToOne(mappedBy = "usuario")
    private Motorista motorista;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> set) {
		this.roles = set;
	}
	
	public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(loginRequest.senha(), getSenha());
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}
}