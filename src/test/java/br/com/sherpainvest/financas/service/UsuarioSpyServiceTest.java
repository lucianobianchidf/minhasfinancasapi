package br.com.sherpainvest.financas.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.repository.UsuarioRepository;
import br.com.sherpainvest.financas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioSpyServiceTest {
	
	// Mock spy pode mockar somente um método específico, mantendo o restante original
	@SpyBean
	UsuarioServiceImpl usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test
	public void salvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
							.id(1l)
							.nome("Luciano")
							.email("lucianobianchidf@gmail.com")
							.senha("123456").build();
		
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());
		
		//verificação
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(1l, usuarioSalvo.getId());
		Assertions.assertEquals("Luciano", usuarioSalvo.getNome());
		Assertions.assertEquals("lucianobianchidf@gmail.com", usuarioSalvo.getEmail());
		Assertions.assertEquals("123456", usuarioSalvo.getSenha());
	}
	
	@Test
	public void lancarErroAoSalvarUsuarioEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			//cenario
			String email = "lucianobianchidf@gmail.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
			
			//ação
			usuarioService.salvarUsuario(usuario);
			
			//verificação
			Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
		});
	}

}
