package com.dg.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dg.model.Role;
import com.dg.model.User;
import com.dg.repository.UserRepository;
import jakarta.transaction.Transactional;

@Configuration
public class AdminConfig implements CommandLineRunner {

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		var userAdmin = userRepository.findByEmail("alan.kostta14@gmail.com");

		userAdmin.ifPresentOrElse(user -> {
			System.out.println("admin já existe!");
		}, () -> {
			var user = new User();
			user.setFullName("admin");
			user.setEmail("alan.kostta14@gmail.com");
			user.setPassword(passwordEncoder.encode("123456"));
			user.setRole(Role.ROLE_ADMIN);
			user.setUpdatedDate(LocalDateTime.now());
			user.setCreatedDate(LocalDateTime.now());
			userRepository.save(user);
		});

	}
}
