package com.XzaoDoMal.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.XzaoDoMal.modelo.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByLogin(String login);

	Optional<Usuario> findByLoginContainingIgnoreCase(String login);
}
