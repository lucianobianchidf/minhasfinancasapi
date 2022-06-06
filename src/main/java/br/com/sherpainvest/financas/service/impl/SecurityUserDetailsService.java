package br.com.sherpainvest.financas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		 Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado."));
		 
		 return User.builder()
				 	.username(usuario.getEmail())
				 	.password(usuario.getSenha())
				 	.roles("USER").build();
	}

}
