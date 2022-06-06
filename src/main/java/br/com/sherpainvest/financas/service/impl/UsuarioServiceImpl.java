package br.com.sherpainvest.financas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sherpainvest.financas.exception.ErroAutenticacaoException;
import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.repository.UsuarioRepository;
import br.com.sherpainvest.financas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PasswordEncoder encoder;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
		super();
		this.usuarioRepository = usuarioRepository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		
		if (!usuario.isPresent())
			throw new ErroAutenticacaoException("Usuário não encontrado.");
		
		boolean isSenhaValida = encoder.matches(senha,  usuario.get().getSenha());
		
		if (!isSenhaValida)
			throw new ErroAutenticacaoException("Senha inválida.");
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		return usuarioRepository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}

	@Override
	public void validarEmail(String email) {
		boolean existeEmail = usuarioRepository.existsByEmail(email);
		
		if (existeEmail)
			throw new RegraNegocioException("Já existe um usuário criado com este email");
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return usuarioRepository.findById(id);
	}

}
