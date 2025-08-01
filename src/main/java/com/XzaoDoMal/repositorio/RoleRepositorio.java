package com.XzaoDoMal.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.XzaoDoMal.modelo.Role;

@Repository
public interface RoleRepositorio extends JpaRepository<Role, Long> {

	Role findByNome(String nome);
}
