package com.XzaoDoMal.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.XzaoDoMal.modelo.Role;
import com.XzaoDoMal.modelo.Usuario;
import com.XzaoDoMal.repositorio.RoleRepositorio;
import com.XzaoDoMal.repositorio.UsuarioRepositorio;

import jakarta.transaction.Transactional;

@Configuration
public class AdminUserConfig implements CommandLineRunner {
	
	@Autowired
	private RoleRepositorio roleRepositorio;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UsuarioRepositorio userRepositorio;
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		
		
		var roleAdmin = roleRepositorio.findByNome(Role.Values.ADMIN.name());
		var userAdmin = userRepositorio.findByLogin("admin");
		
		userAdmin.ifPresentOrElse(
				user -> {
					System.out.println("admin já existe!");
					},
					()-> {
						var user = new Usuario();
						user.setLogin("admin");
						user.setSenha(passwordEncoder.encode("123"));
						user.setRoles(Set.of(roleAdmin));
						userRepositorio.save(user);
					} 
				);
		
	}

}
