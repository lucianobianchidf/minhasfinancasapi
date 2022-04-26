package br.com.sherpainvest.financas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.sherpainvest.financas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarExistenciaDeUmEmail() { 
		//cenário 
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
	  
		//ação/execução 
		boolean resultado = usuarioRepository.existsByEmail("lucianobianchidf@gmail.com");
	  
		//verificação 
		Assertions.assertThat(resultado).isTrue(); 
	}
	
	@Test
	public void retornarFalsoUsuarioCadastradoEmail() {
		//cenário
		
		//ação/execução
		boolean resultado = usuarioRepository.existsByEmail("lucianobianchidf@gmail.com");
		
		//verificação
		Assertions.assertThat(resultado).isFalse();
	}
	
	@Test
	public void persistirUsuarioBaseDados() {
		//cenário
		Usuario usuario = criarUsuario();
		
		//ação
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		//verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void buscarUsuarioPorEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificação
		Optional<Usuario> resultado = usuarioRepository.findByEmail("lucianobianchidf@gmail.com");
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("Luciano")
				.email("lucianobianchidf@gmail.com")
				.senha("123456")
				.build();
	}

}
