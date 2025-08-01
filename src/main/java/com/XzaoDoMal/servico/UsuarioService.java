package com.XzaoDoMal.servico;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.XzaoDoMal.modelo.Usuario;
import com.XzaoDoMal.repositorio.UsuarioRepositorio;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepositorio userRepositorio;
	
	@Transactional
	public Usuario salvarUsuario(Usuario user) {
		return userRepositorio.saveAndFlush(user);
	}// Retorna o objeto salvo

	public Page<Usuario> pesquisarTodosMotoristas(Pageable pageable) {
	    return userRepositorio.findAll(pageable);
	}
	public List<Usuario> todosUsuarios(){
		return userRepositorio.findAll();
	}

	public Optional<Usuario> pesquisarUsuarioPorLogin(String login) {
		return userRepositorio.findByLoginContainingIgnoreCase(login); // Retorno direto
	}
	
	public Optional<Usuario> pesquisarUsuarioPorId(Long id) {
		if (userRepositorio.existsById(id)) {
			return userRepositorio.findById(id);
		}
		return null; // Retorna Optional<Motorista>
	}
	@Transactional
	public void removerUsuarioPorId(Long id) {
	    try {
	        if (userRepositorio.existsById(id)) {
	        	userRepositorio.deleteById(id);
	        } else {
	            throw new TratamentoMotorista("Usuário com código " + id + " não foi localizado");
	        }
	    } catch (TratamentoMotorista e) {
	        // Tratar a exceção, exibir uma mensagem ao usuário ou registrar log
	        System.out.println(e.getMessage());
	    }
	}
	public boolean confirmaSenha(Usuario user) {
		if(!user.getSenha().equals(user.getConfirmaSenha())) {
			return false;
		}
		return true;
	}
}
