package com.XzaoDoMal.controle;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.XzaoDoMal.modelo.Role;
import com.XzaoDoMal.modelo.Usuario;
import com.XzaoDoMal.repositorio.RoleRepositorio;
import com.XzaoDoMal.servico.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'BASIC', 'MOTORISTA')")
public class IndexControle {
	
	@Autowired
	private UsuarioService userService;
	
	@Autowired
	private RoleRepositorio roleRepositorio;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
		
	@GetMapping("/inicio-controle")
	public ModelAndView inicio() {
	    ModelAndView mv = new ModelAndView("index");
	    return mv;
	}
	@GetMapping("/inicio-login")
	public ModelAndView inicioLogin() {
	    ModelAndView mv = new ModelAndView("login-acesso");
	    return mv;
	}
	@GetMapping("/cadastro-user")
	public ModelAndView cadastroUser() {
	    ModelAndView mv = new ModelAndView("cadastro-login");
	    Usuario user = new Usuario();
	    List<Role> roles = roleRepositorio.findAll();
	    roles.removeIf(role -> 
	    	role.getNome().equals(Role.Values.ADMIN.name()));
	    mv.addObject("roles", roles);
	    mv.addObject("user", user);
	   
	    return mv;
	}
	@PostMapping("/salvar-usuario")
	@Transactional
	public ModelAndView newUser(@Valid Usuario user, BindingResult result, RedirectAttributes redirectAttributes) {
		
		ModelAndView mv = new ModelAndView();
		
		if (result.hasErrors()) {
			mv.setViewName("cadastro-user");
            return mv;
        }
//		var motoristaRole = roleRepositorio.findByNome(Role.Values.MOTORISTA.name());
		var userFromDb = userService.pesquisarUsuarioPorLogin(user.getLogin());
		
		if(userFromDb.isPresent()) {
			  mv.setViewName("/cadastro-user"); // Nome da sua página de formulário
		      mv.addObject("usuario", user);  // Devolve o usuário para reuso dos dados
		      mv.addObject("erro", "Login já está em uso. Escolha outro.");
		      return mv;
		}else if(!userService.confirmaSenha(user)) {
			  mv.setViewName("/cadastro-user"); // Nome da sua página de formulário
		      mv.addObject("usuario", user);  // Devolve o usuário para reuso dos dados
		      mv.addObject("erro", "As senhas não conferem.");
		      return mv;
		}
		user.setSenha(passwordEncoder.encode(user.getSenha()));
//		user.setRoles(Set.of(motoristaRole));
		
		userService.salvarUsuario(user);
		redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso!");
		mv.setViewName("redirect:/inicio-login");
		return mv;
	}

	@GetMapping("/erro-autenticaco")
	public ModelAndView erro() {
	    ModelAndView mv = new ModelAndView("erroAutenticacao");
	    return mv;
	}
}

