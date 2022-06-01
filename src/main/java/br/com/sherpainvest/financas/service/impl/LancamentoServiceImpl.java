package br.com.sherpainvest.financas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.enums.StatusLancamento;
import br.com.sherpainvest.financas.model.enums.TipoLancamento;
import br.com.sherpainvest.financas.model.repository.LancamentoRepository;
import br.com.sherpainvest.financas.model.repository.LancamentoRepositoryCustom;
import br.com.sherpainvest.financas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional 
	public Lancamento atualizar(Lancamento lancamento) {
		validar(lancamento);
		Objects.requireNonNull(lancamento.getId());
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		return lancamentoRepository.buscarLancamentos(lancamentoFiltro);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
		lancamento.setStatus(statusLancamento);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().isEmpty()) 
			throw new RegraNegocioException("Informe uma descrição válida.");
		
		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) 
			throw new RegraNegocioException("Informe um mês válido.");
		
		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4)
			throw new RegraNegocioException("Informe um ano válido.");
		
		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null)
			throw new RegraNegocioException("Informe um usuário.");
		
		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1)
			throw new RegraNegocioException("Informe um valor válido.");
			
		if (lancamento.getTipo() == null)
			throw new RegraNegocioException("Informe um tipo de lançamento.");
	}

	@Override
	public Optional<Lancamento> buscarPorId(Long idLancamento) {
		return lancamentoRepository.findById(idLancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(id, TipoLancamento.RECEITA);
		BigDecimal despesas = lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(id, TipoLancamento.DESPESA);
		
		if (receitas == null)
			receitas = BigDecimal.ZERO;
		
		if (despesas == null)
			despesas = BigDecimal.ZERO;
		
		return receitas.subtract(despesas);
	}

}
