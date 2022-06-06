package br.com.sherpainvest.financas.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sherpainvest.financas.api.dto.UsuarioDTO;
import br.com.sherpainvest.financas.exception.ErroAutenticacaoException;
import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.service.LancamentoService;
import br.com.sherpainvest.financas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {
	
	static final String API = "/api/usuarios";
	
	static final MediaType MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService usuarioService;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		String email = "lucianobianchidf@gmail.com";
		String senha = "123";
		
		//cenario
		UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).nome("Luciano").email(email).senha(senha).build();

		Mockito.when(usuarioService.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(usuarioDTO);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticacao"))
			.accept(MEDIA_TYPE_JSON)
			.contentType(MEDIA_TYPE_JSON)
			.content(json);
			
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()));		
	}
	
	@Test
	public void deveRetornarBadRequestAoAutenticarUmUsuario() throws Exception {
		String email = "lucianobianchidf@gmail.com";
		String senha = "123";
		
		//cenario
		UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();

		Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(usuarioDTO);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticacao"))
			.accept(MEDIA_TYPE_JSON)
			.contentType(MEDIA_TYPE_JSON)
			.content(json);
			
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		String email = "lucianobianchidf@gmail.com";
		String senha = "123";
		
		//cenario
		UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).nome("Luciano").email(email).senha(senha).build();

		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(usuarioDTO);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(MEDIA_TYPE_JSON)
			.contentType(MEDIA_TYPE_JSON)
			.content(json);
			
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()));		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUsuarioInvalido() throws Exception {
		String email = "lucianobianchidf@gmail.com";
		String senha = "123";
		
		//cenario
		UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();

		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(usuarioDTO);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(MEDIA_TYPE_JSON)
			.contentType(MEDIA_TYPE_JSON)
			.content(json);
			
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
