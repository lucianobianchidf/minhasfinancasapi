package br.com.sherpainvest.financas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);
	
	void validar(Lancamento lancamento);

	Optional<Lancamento> buscarPorId(Long idLancamento);
	
	BigDecimal obterSaldoPorUsuario(Long id);

}