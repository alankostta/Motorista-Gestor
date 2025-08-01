package com.XzaoDoMal.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.XzaoDoMal.modelo.Despesa;
@Repository
public interface DespesaRepositorio extends JpaRepository<Despesa, Long> {

	Page<Despesa> findAll(Pageable pageable);
}
