package com.XzaoDoMal.servico;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.XzaoDoMal.modelo.ControleGeral;
import com.XzaoDoMal.modelo.Despesa;
import com.XzaoDoMal.modelo.Motorista;
import com.XzaoDoMal.repositorio.MotoristaRepositorio;

@Service
public class MotoristaService {

	@Autowired
	private MotoristaRepositorio motoristaRepositorio;

	@Transactional
	public Motorista salvarMotorista(Motorista motorista) {
		
		motorista.setNome(motorista.getNome().toUpperCase());
    	motorista.setCarro(motorista.getCarro().toUpperCase());
		return motoristaRepositorio.save(motorista);
	}// Retorna o objeto salvo

	public Page<Motorista> pesquisarTodosMotoristas(Pageable pageable) {
	    return motoristaRepositorio.findAll(pageable);
	}
	public List<Motorista> todosMotoristas(){
		return motoristaRepositorio.findAll();
	}

	public List<Motorista> pesquisarMotoristasPorNome(String nome) {
		return motoristaRepositorio.findByNomeContainingIgnoreCase(nome); // Retorno direto
	}
	public List<Motorista> pesquisarMotoristasPorCarro(String carro) {
		return motoristaRepositorio.findByCarroContainingIgnoreCase(carro); // Retorno direto
	}
	public List<Motorista> pesquisarDespesasMotorista(Despesa despesa) {
		return motoristaRepositorio.findByDespesa(despesa); // Retorno direto
	}
	public List<Motorista> pesquisaRotinaMotorista(ControleGeral controle) {
		return motoristaRepositorio.findByControle(controle); // Retorno direto
	}
	public Optional<Motorista> pesquisarMotoristaPorId(Long id) {
		if (motoristaRepositorio.existsById(id)) {
			return motoristaRepositorio.findById(id);
		}
		return null; // Retorna Optional<Motorista>
	}
	@Transactional
	public void removerMotoristaPorId(Long id) {
	    try {
	        if (motoristaRepositorio.existsById(id)) {
	            motoristaRepositorio.deleteById(id);
	        } else {
	            throw new TratamentoMotorista("Motorista com código " + id + " não foi localizado");
	        }
	    } catch (TratamentoMotorista e) {
	        // Tratar a exceção, exibir uma mensagem ao usuário ou registrar log
	        System.out.println(e.getMessage());
	    }
	}
}
