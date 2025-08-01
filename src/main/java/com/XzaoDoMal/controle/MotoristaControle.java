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
import com.XzaoDoMal.modelo.Motorista;
import com.XzaoDoMal.modelo.Usuario;
import com.XzaoDoMal.repositorio.UsuarioRepositorio;
import com.XzaoDoMal.servico.MotoristaService;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/motorista")
@PreAuthorize("hasAnyRole('ADMIN', 'BASIC', 'MOTORISTA')")
public class MotoristaControle {
     
    @Autowired
    MotoristaService motoristaService;
    
    @Autowired
    UsuarioRepositorio userRepositorio;
    
    @GetMapping("/cadastro-motorista")
    public ModelAndView inicioMotorista(
            Motorista motorista,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        
        // Configuração de ordenação
        Sort sortOrder = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size, sortOrder);
        
        Page<Motorista> motoristasPage = motoristaService.pesquisarTodosMotoristas(pageable);
        List<Usuario> users = userRepositorio.findAll();
        
        if (page >= motoristasPage.getTotalPages() && motoristasPage.getTotalPages() > 0) {
            pageable = PageRequest.of(motoristasPage.getTotalPages() - 1, size, sortOrder);
            motoristasPage = motoristaService.pesquisarTodosMotoristas(pageable);
        }
        
        ModelAndView mv = new ModelAndView("motorista/cadastro-motorista");
        mv.addObject("motorista", motorista);
        mv.addObject("users", users);
        mv.addObject("motoristas", motoristasPage.getContent());
        mv.addObject("currentPage", motoristasPage.getNumber());
        mv.addObject("totalPages", motoristasPage.getTotalPages());
        mv.addObject("totalElements", motoristasPage.getTotalElements());
        mv.addObject("sort", sort);
        mv.addObject("direction", direction);
        return mv;
    }


    @PostMapping("/salvar")
    @Transactional
    public ModelAndView salvarMotorista(Motorista motorista) {
    	motoristaService.salvarMotorista(motorista);
        return new ModelAndView("redirect:cadastro-motorista");
    }

    @GetMapping("/editarMotorista/{id}")
    @Transactional
    public ModelAndView editarMotorista(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        
        Optional<Motorista> motoristaOpt = motoristaService.pesquisarMotoristaPorId(id);
        if (motoristaOpt.isPresent()) {
            // Cria o objeto Pageable com ordenação
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            @SuppressWarnings("unused")
			Pageable pageable = PageRequest.of(page, size, sort);

            ModelAndView mv = inicioMotorista(motoristaOpt.get(), page, size, sortBy, direction);
            mv.addObject("motorista", motoristaOpt.get()); // Carrega o motorista para edição
            return mv;
        } else {
            return new ModelAndView("redirect:/motorista/cadastro-motorista?error=MotoristaNaoEncontrado");
        }
    }

    @GetMapping("/pesquisar")
    @Transactional
    public ModelAndView pesquisarMotorista(
    		@RequestParam String tipo,
    		@RequestParam String valor,
    		Motorista motorista,
    		 @RequestParam(defaultValue = "0") int page) {
    	ModelAndView mv = new ModelAndView("motorista/cadastro-motorista");
        List<Motorista> motoristas = new ArrayList<>();

        if ("nome".equals(tipo)) {
            if (valor == null || valor.isBlank()) {
                mv.addObject("mensagem", "O nome não pode estar em branco ou nulo.");
            } else {
                motoristas = motoristaService.pesquisarMotoristasPorNome(valor);
            }
        } else if ("carro".equals(tipo)) {
            if (valor == null || valor.isBlank()) {
                mv.addObject("mensagem", "O carro não pode estar em branco ou nulo.");
            } else {
                motoristas = motoristaService.pesquisarMotoristasPorCarro(valor);
            }
        } else if ("id".equals(tipo)) {
            if (valor == null || valor.isBlank()) {
                mv.addObject("mensagem", "O ID não pode estar em branco ou nulo.");
            } else {
                try {
                    Long id = Long.valueOf(valor);
                    Optional<Motorista> moto = motoristaService.pesquisarMotoristaPorId(id);
                    moto.ifPresent(motoristas::add);
                } catch (NumberFormatException e) {
                    mv.addObject("mensagem", "O ID deve ser um número válido.");
                }
            }
        } else {
            mv.addObject("mensagem", "Tipo de filtro inválido. Selecione 'nome', 'carro' ou 'id'.");
        }

        // Ajustando dados padrão no retorno
        mv.addObject("motoristas", motoristas);
        mv.addObject("motorista", motorista);
        mv.addObject("currentPage", page);
        int totalPages = (motoristas.size() + 4) / 5; // Exemplo: 5 motoristas por página
        mv.addObject("totalPages", totalPages);

        return mv;

    }

    @GetMapping("/excluirMotorista/{id}")
    @Transactional
    public ModelAndView excluirMotorista(@PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "nome") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
    	motoristaService.removerMotoristaPorId(id);
    	ModelAndView mv = inicioMotorista(new Motorista(), page, size, sortBy, direction);
        return mv;
    }
}

