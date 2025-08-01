package com.XzaoDoMal.servico;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.XzaoDoMal.modelo.Despesa;
import com.XzaoDoMal.repositorio.DespesaRepositorio;
import com.XzaoDoMal.repositorio.MotoristaRepositorio;

@Service
public class DespesaService {
	
	@Autowired
	MotoristaRepositorio motoristaRepositorio;
	
	@Autowired
	DespesaRepositorio despesaRepositorio;
	
	public void salvarDespesas(List<Despesa> despesas, String acao, Despesa despesa) {

		 if (acao.equals("qtdDespesa")) {
			 despesa.setDescricao(despesa.getDescricao().toUpperCase());
			 despesas.add(despesa);
		}else if (acao.equals("salvar")) {
			despesaRepositorio.saveAll(despesas);
			despesas.clear();
		}
	}
	public Page<Despesa> carregarTodasDespesas(Pageable pageable) {
	    return despesaRepositorio.findAll(pageable);
	}
}
