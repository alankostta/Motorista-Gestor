package com.XzaoDoMal.repositorio;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.XzaoDoMal.modelo.ControleGeral;
import com.XzaoDoMal.modelo.Despesa;
import com.XzaoDoMal.modelo.Motorista;

@Repository
public interface MotoristaRepositorio extends JpaRepository<Motorista, Long>{

	Page<Motorista> findAll(Pageable pageable);
	
	List<Motorista> findAll();
	
	List<Motorista> findByNomeContainingIgnoreCase(String nome);
	
	List<Motorista> findByCarroContainingIgnoreCase(String carro);
	
	List<Motorista> findByDespesa(Despesa despesa);	
	
	List<Motorista> findByControle(ControleGeral controle);

}
