package com.XzaoDoMal.servico;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.XzaoDoMal.modelo.Usuario;
import com.XzaoDoMal.repositorio.UsuarioRepositorio;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
        	    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNome()))
        	    .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            usuario.getLogin(),
            usuario.getSenha(),
            authorities
        );
    }
}

