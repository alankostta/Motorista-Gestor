package com.XzaoDoMal.controle;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
import com.XzaoDoMal.modelo.ControleDeHoras;
import com.XzaoDoMal.modelo.ControleGeral;
import com.XzaoDoMal.modelo.Motorista;
import com.XzaoDoMal.servico.ControleGeralService;
import com.XzaoDoMal.servico.MotoristaService;
import jakarta.transaction.Transactional;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'BASIC', 'MOTORISTA')")
@RequestMapping("/cadastro-controle-geral")
public class ControleGeralControle {

	@Autowired
	MotoristaService motoristaService;
	
	@Autowired
	ControleGeralService controleGeralService;

	@GetMapping("/controle-geral")
	public ModelAndView inicioControleGeral(ControleGeral controleGeral,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
		
		  // Configuração de ordenação
        Sort sortOrder = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size, sortOrder);
        
        Page<ControleGeral> controlePage = controleGeralService.pesquisarTodosControles(pageable);
        
        if (page >= controlePage.getTotalPages() && controlePage.getTotalPages() > 0) {
            pageable = PageRequest.of(controlePage.getTotalPages() - 1, size, sortOrder);
            controlePage = controleGeralService.pesquisarTodosControles(pageable);
        }
		
		ModelAndView mv = new ModelAndView("/controle-geral/cadastro-controle-geral");
		List<Motorista> motoristas = motoristaService.todosMotoristas();
		mv.addObject("controleGeral", controleGeral);
		mv.addObject("motoristas", motoristas);	
        mv.addObject("controleGerais", controlePage.getContent());
        mv.addObject("currentPage", controlePage.getNumber());
        mv.addObject("totalPages", controlePage.getTotalPages());
        mv.addObject("totalElements", controlePage.getTotalElements());
        mv.addObject("sort", sort);
        mv.addObject("direction", direction);
		return mv;
	}

	@PostMapping("/salvarControleGeral")
	@Transactional
	public ModelAndView salvarControleGeral(ControleGeral controleGeral) {
		controleGeralService.salvarControle(controleGeral);
		return new ModelAndView("redirect:/cadastro-controle-geral/controle-geral");
	}

	@GetMapping("/editarControleGeral/{id}")
	@Transactional
	public ModelAndView editarControleGeral(@PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
		Optional<ControleGeral> controlegeralOpt = controleGeralService.pesquisaControlePorId(id);
		if (controlegeralOpt.isPresent()) {
			 // Cria o objeto Pageable com ordenação
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            @SuppressWarnings("unused")
			Pageable pageable = PageRequest.of(page, size, sort);

            ModelAndView mv = inicioControleGeral(controlegeralOpt.get(), page, size, sortBy, direction);
            mv.addObject("controle", controlegeralOpt.get()); // Carrega o motorista para edição
            return mv;
		} else {
			// Redireciona para uma página de erro personalizada ou retorna uma mensagem de
			// erro
			return new ModelAndView("redirect:controle-geral/cadastro-controle-geral");
		}
	}

	@GetMapping("/excluirControleGeral/{id}")
	@Transactional
	public ModelAndView excluirControleGeral(@PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
		controleGeralService.removerControlePorId(id);
		return inicioControleGeral(new ControleGeral(),page, size, sortBy, direction);
	}

	@GetMapping("/horas/{id}")
	public ModelAndView horasTrabalhadas(@PathVariable("id") Long id) {
		// Busca o registro específico pelo ID
		Optional<ControleGeral> controlegeralOpt = controleGeralService.pesquisaControlePorId(id);

		if (controlegeralOpt.isPresent()) {
			// Registro encontrado
			ControleGeral controleGeral = controlegeralOpt.get();

			// Cria uma lista contendo apenas o registro encontrado
			List<ControleGeral> listaControleGeral = List.of(controleGeral);

			// Inicializa os objetos ControleDeHoras usando apenas o registro específico
			ControleDeHoras horasSemana = new ControleDeHoras(listaControleGeral);
			ControleDeHoras horasMes = new ControleDeHoras(listaControleGeral);
			ControleDeHoras horasAno = new ControleDeHoras(listaControleGeral);
			String horasDia = controleGeral.getDuracaoFormatada();
			
			// Calcula as durações

			// Cria o ModelAndView para retornar a página com os dados
			ModelAndView mv = new ModelAndView("controle-geral/horas-trabalhadas");
			mv.addObject("horasDia", horasDia);
			
			mv.addObject("horasSemana", horasSemana.formatarDuracao(horasSemana.calcularDuracaoSemana()));
			mv.addObject("horasMes", horasMes.formatarDuracao(horasMes.calcularDuracaoMes()));
			mv.addObject("horasAno", horasAno.formatarDuracao(horasAno.calcularDuracaoAno()));

			return mv;
		} else {
			// Caso o registro não seja encontrado, redireciona para uma página de erro ou
			// cadastro
			return new ModelAndView("redirect:/controle-geral/cadastro-controle-geral");
		}
	}

	@GetMapping("/horas")
	public ModelAndView horasTrabalhada() {

		ControleGeral controle = new ControleGeral();
		controle.setData(new Date());
		List<ControleGeral> listaControleGeral = controleGeralService.pesquisarControles();

		// Inicializa os objetos ControleDeHoras usando apenas o registro específico
		ControleDeHoras horasSemana = new ControleDeHoras(listaControleGeral);
		ControleDeHoras horasMes = new ControleDeHoras(listaControleGeral);
		ControleDeHoras horasAno = new ControleDeHoras(listaControleGeral);
		String horasDia = controle.getDuracaoFormatada();
		controle.setInicioJornada(LocalDateTime.of(2024, 9, 1, 9, 0));
		
		LocalDateTime inicioJornada = controle.getInicioJornada();
		
		Date inicioJornadaDate = Date.from(inicioJornada.atZone(ZoneId.systemDefault()).toInstant());
		Long num = (long) 1;
		controle.setId(num);
//		Double horaDia = controleGeralService.getHorasDiaPorId(controle.getId().intValue(), inicioJornadaDate);
		Double horaGeral = controleGeralService.getHorasDiaGeral(inicioJornadaDate);
		
		// Cria o ModelAndView para retornar a página com os dados
		ModelAndView mv = new ModelAndView("controle-geral/horas-trabalhadas");
		mv.addObject("horaGeral", horaGeral);
		mv.addObject("controle", controle);
		mv.addObject("horasDia", horasDia); // Usando o registro específico para horas do // dia
		mv.addObject("horasSemana", horasSemana.formatarDuracao(horasSemana.calcularDuracaoSemana()));
		mv.addObject("horasMes", horasMes.formatarDuracao(horasMes.calcularDuracaoMes()));
		mv.addObject("horasAno", horasAno.formatarDuracao(horasAno.calcularDuracaoAno()));

		return mv;
	}

	@GetMapping("/ganhos")
	public ModelAndView ganhos() {
		ModelAndView mv = new ModelAndView("controle-geral/dados-analiticos");
		return mv;
	}
	
//	@GetMapping("/horas-dia-id")
//	public Double calculoHorasDiaPorId(@RequestParam Long id, LocalDateTime data ) {
//		return controleGeralService.getHorasDiaPorId(id, data);
//		
//	}
}
