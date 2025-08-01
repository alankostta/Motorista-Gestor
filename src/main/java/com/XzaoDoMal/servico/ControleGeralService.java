package com.XzaoDoMal.servico;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.XzaoDoMal.modelo.ControleGeral;
import com.XzaoDoMal.repositorio.ControleGeralRepositorio;

@Service
public class ControleGeralService {
	
	@Autowired
	ControleGeralRepositorio controleGeralRepositorio;
	
	@Transactional
	public ControleGeral salvarControle(ControleGeral controle) {
		if (controle == null) {
			return null;
		}
		
		return controleGeralRepositorio.save(controle); // Retorna o objeto salvo
	}
	public List<ControleGeral> pesquisarControles(){
		return controleGeralRepositorio.findAll();
	}

	public Page<ControleGeral> pesquisarTodosControles(Pageable pageable) {
	    return controleGeralRepositorio.findAll(pageable);
	}
	public Optional<ControleGeral> pesquisaControlePorId(Long id) {
		if (controleGeralRepositorio.existsById(id)) {
			return controleGeralRepositorio.findById(id);
		}
		return null; // Retorna Optional<Motorista>
	}
	@Transactional
	public void removerControlePorId(Long id) {
	    try {
	        if (controleGeralRepositorio.existsById(id)) {
	        	controleGeralRepositorio.deleteById(id);
	        } else {
	            throw new TratamentoMotorista("Controle com código " + id + " não foi localizado");
	        }
	    } catch (TratamentoMotorista e) {
	        // Tratar a exceção, exibir uma mensagem ao usuário ou registrar log
	        System.out.println(e.getMessage());
	    }
	}
	public Double getHorasDiaPorId(Integer id, Date data) {
		return controleGeralRepositorio.calcularHorasDiaPorId(id, data);
	}
	public Double getHorasDiaGeral(Date data) {
		return controleGeralRepositorio.calcularHorasDiaGeral(data);
	}

}
