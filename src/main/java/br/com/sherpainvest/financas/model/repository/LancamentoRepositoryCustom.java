package br.com.sherpainvest.financas.model.repository;

import java.util.List;

import br.com.sherpainvest.financas.model.entity.Lancamento;

public interface LancamentoRepositoryCustom {
	
	public List<Lancamento> buscarLancamentos(Lancamento lancamentoFiltro);

}
