package br.com.sherpainvest.financas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockitoSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.enums.StatusLancamento;
import br.com.sherpainvest.financas.model.repository.LancamentoRepository;
import br.com.sherpainvest.financas.model.repository.LancamentoRepositoryTest;
import br.com.sherpainvest.financas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execução
		service.atualizar(lancamentoSalvo);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//execução e verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarLancamentoAindaNaoSalvo() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		//execução e verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//execução
		service.deletar(lancamento);
		
		//verificação		
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoNaoSalvo() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		
		//verificação		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltarLancamentos() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verficiação
		Assertions.assertThat(resultado)
						.isNotEmpty()
						.hasSize(1)
						.contains(lancamento);
	}
	
	public void deveAtualizarStatusLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//execução
		service.atualizarStatus(lancamento, StatusLancamento.EFETIVADO);
		// OBS: COMO ESTOU TESTANDO SOMENTE A PARTE DE ATUALIZAR STATUS NÃO PRECISA EFETIVAMENTE CHAMAR O ATUALIZAR, SOMENTE VIA MOCK
		// ESTOU USANDO O SPY BEAN
		Mockito.doNothing().when(service).atualizar(lancamento);
		
		//verificação
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.EFETIVADO);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterLancamentoPorId() {
		//cenario
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execução
		Optional<Lancamento> lancamentoEncontrado = service.buscarPorId(id);
		
		//verificação	
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetonarVazioQuandoLancamentoInexistente() {
		//cenario
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(1l)).thenReturn(Optional.empty());
		
		//execução
		Optional<Lancamento> lancamentoEncontrado = service.buscarPorId(id);
		
		//verificação	
		assertThat(lancamentoEncontrado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarExcecaoAoValidarUsuarioDoLancamentoNaoInformado() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução
		Throwable excecao = Assertions.catchThrowable(() -> service.validar(lancamento));
		
		//verificação
		assertThat(excecao).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
	}

}
