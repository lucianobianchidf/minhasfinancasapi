package br.com.sherpainvest.financas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.repository.UsuarioRepository;
import br.com.sherpainvest.financas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@Autowired
	UsuarioService usuarioService;
		
	@Autowired
	UsuarioRepository usuarioRepository;
	
	UsuarioService usuarioService4Mock;
	
	UsuarioRepository usuarioRepository4Mock;
	
	@BeforeEach
	public void setUp() {
		usuarioRepository4Mock = Mockito.mock(UsuarioRepository.class);
		usuarioService4Mock = new UsuarioServiceImpl(usuarioRepository4Mock);
	}
	
	@Test
	public void validarEmailUsuarioComMock() {		
		Assertions.assertDoesNotThrow(() -> {
			Mockito.when(usuarioRepository4Mock.existsByEmail(Mockito.anyString())).thenReturn(false);
			usuarioService4Mock.validarEmail("lucianobianchidf@gmail.com");			
		});
	}
	
	@Test
	public void validarEmailUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			usuarioRepository.deleteAll();
			
			usuarioService.validarEmail("lucianobianchidf@gmail.com");			
		});
	}
	
	@Test
	public void lancarErroAoValidarEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			Usuario usuario = Usuario.builder().nome("Luciano").email("lucianobianchidf@gmail.com").build();
			usuarioRepository.save(usuario);
			
			usuarioService.validarEmail("lucianobianchidf@gmail.com");			
		});
	}

}
