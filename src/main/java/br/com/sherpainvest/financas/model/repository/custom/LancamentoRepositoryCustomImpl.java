package br.com.sherpainvest.financas.model.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.repository.LancamentoRepositoryCustom;

public class LancamentoRepositoryCustomImpl implements LancamentoRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Lancamento> buscarLancamentos(Lancamento lancamentoFiltro) {
		StringBuilder sql = new StringBuilder("from Lancamento");
		sql.append(" where ano = :ano");
		
		if (lancamentoFiltro.getDescricao() != null && !lancamentoFiltro.getDescricao().isBlank()) 
			sql.append(" and descricao like :descricao");
		
		if (lancamentoFiltro.getUsuario() != null)
			sql.append(" and usuario.id = :idUsuario");
		
		if (lancamentoFiltro.getMes() != null && lancamentoFiltro.getMes() > 0)
			sql.append(" and mes = :mes");
		
		Query query = entityManager.createQuery(sql.toString());
		
		query.setParameter("ano", lancamentoFiltro.getAno());
		
		if (lancamentoFiltro.getDescricao() != null && !lancamentoFiltro.getDescricao().isEmpty())
			query.setParameter("descricao", "like '%" + lancamentoFiltro.getDescricao() + "%'");

		if (lancamentoFiltro.getUsuario() != null)
			query.setParameter("idUsuario", lancamentoFiltro.getUsuario().getId());
			
		if (lancamentoFiltro.getMes() != null && lancamentoFiltro.getMes() > 0) 
			query.setParameter("mes", lancamentoFiltro.getMes());
		
		List<Lancamento> lancamentos = query.getResultList();
		
		return lancamentos;
	}

}
