package com.XzaoDoMal.controle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.XzaoDoMal.modelo.Despesa;
import com.XzaoDoMal.modelo.Motorista;
import com.XzaoDoMal.modelo.TipoDespesa;
import com.XzaoDoMal.repositorio.DespesaRepositorio;
import com.XzaoDoMal.repositorio.MotoristaRepositorio;
import com.XzaoDoMal.servico.DespesaService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/despesas")
@PreAuthorize("hasAnyRole('ADMIN', 'BASIC', 'MOTORISTA')")
public class DespesaControle {

	@Autowired
	private MotoristaRepositorio motoristaRepositorio;
	@Autowired
	private DespesaRepositorio despesaRepositorio;

	@Autowired
	DespesaService despesaService;

	List<Despesa> despesas = new ArrayList<>();

	@GetMapping("/cadastro-despesas")
	public ModelAndView inicioDespesa(Despesa despesa) {
		ModelAndView mv = new ModelAndView("despesas/cadastro-despesa");
		List<Motorista> motoristas = motoristaRepositorio.findAll();

		mv.addObject("motoristas", motoristas);
		mv.addObject("despesa", despesa);
		mv.addObject("tiposDespesa", TipoDespesa.values());
		mv.addObject("despesas", this.despesas);
		return mv;
	}

	@GetMapping("/listar-despesas")
	public ModelAndView listarDespesa(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
			@RequestParam(value = "sort", defaultValue = "id") String sort,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {
		// Configuração de ordenação
		Sort sortOrder = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
		Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size, sortOrder);

		Page<Despesa> despesasPage = despesaService.carregarTodasDespesas(pageable);

		if (page >= despesasPage.getTotalPages() && despesasPage.getTotalPages() > 0) {
			pageable = PageRequest.of(despesasPage.getTotalPages() - 1, size, sortOrder);
			despesasPage = despesaService.carregarTodasDespesas(pageable);
		}

		ModelAndView mv = new ModelAndView("despesas/listar-despesa");

		mv.addObject("despesas", despesasPage.getContent());
		mv.addObject("currentPage", despesasPage.getNumber());
		mv.addObject("totalPages", despesasPage.getTotalPages());
		mv.addObject("totalElements", despesasPage.getTotalElements());
		mv.addObject("sort", sort);
		mv.addObject("direction", direction);
		return mv;
	}

	@PostMapping("/salvarDespesa")
	@Transactional
	public ModelAndView salvarDespesa(ModelAndView mv, Despesa despesa, String acao) {

		despesaService.salvarDespesas(this.despesas, acao, despesa);
		
		mv.setViewName("redirect:/despesas/cadastro-despesas");
		return mv;
	}

	@GetMapping("/editarDespesa/{id}")
	@Transactional
	public ModelAndView editarDespesa(@PathVariable("id") Long id) {
		Optional<Despesa> despesaOpt = despesaRepositorio.findById(id);
		if (despesaOpt.isPresent()) {
			return inicioDespesa(despesaOpt.get());
		} else {
			// Redireciona para uma página de erro personalizada ou retorna uma mensagem de
			// erro
			return new ModelAndView("redirect:/despesas/cadastro-despesas");
		}
	}

	@GetMapping("/excluirDespesa/{id}")
	@Transactional
	public ModelAndView excluirDespesa(@PathVariable("id") Long id) {
		if (despesaRepositorio.existsById(id)) {
			despesaRepositorio.deleteById(id);
		}
		return inicioDespesa(new Despesa());
	}
}
