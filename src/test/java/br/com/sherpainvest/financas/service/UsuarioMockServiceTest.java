package br.com.sherpainvest.financas.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.sherpainvest.financas.exception.ErroAutenticacaoException;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.repository.UsuarioRepository;
import br.com.sherpainvest.financas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioMockServiceTest {
	
	// daria pra usar o SpyBean direto aqui e retirar o setUp()
	UsuarioService usuarioService;
	
	// mocka todos os métodos e chamadas
	// já no mock spy vc pode mockar somente um método específico, o resto funciona como o orignal
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@BeforeEach
	public void setUp() {
		usuarioService = new UsuarioServiceImpl(usuarioRepository);
	}
	
	@Test
	public void validarEmailUsuarioComMock() {		
		Assertions.assertDoesNotThrow(() -> {
			Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
			usuarioService.validarEmail("lucianobianchidf@gmail.com");			
		});
	}
	
	@Test
	public void autenticarUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			//cenário
			String email = "lucianobianchidf@gmail.com";
			String senha = "123456";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			//ação
			Usuario resultado = usuarioService.autenticar(email, senha);
			
			//verificação
			Assertions.assertNotNull(resultado);
		});
	}
	
	@Test
	public void lancarErroQuandoNaoEncontrarUsuarioPorEmail() {
		Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
			//cenário
			Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
			//ação
			usuarioService.autenticar("lucianobianchidf@gmail.com", "123456");
		});
	}
	
	@Test
	public void lancarErroQuandoSenhaInvalida() {
		Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
			//cenário
			String email = "lucianobianchidf@gmail.com";
			String senha = "123456";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			//ação
			usuarioService.autenticar("lucianobianchidf@gmail.com", "987654");
		});
	}
	
	@Test
	public void lancarErroQuandoSenhaInvalidaComCatchException() {
		//cenário
		String email = "lucianobianchidf@gmail.com";
		String senha = "123456";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Throwable excecao = assertThrows(ErroAutenticacaoException.class, 		
											() -> usuarioService.autenticar("lucianobianchidf@gmail.com", "987654"));
		
		assertTrue(excecao.getMessage().equals("Senha inválida."));
	}

}
