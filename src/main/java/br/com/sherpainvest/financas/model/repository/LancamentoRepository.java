package br.com.sherpainvest.financas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.enums.TipoLancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryCustom { 
	
	@Query(value = "select sum(l.valor) from Lancamento l join l.usuario u "
			+ "where u.id = :idUsuario and l.tipo = :tipo "
			+ "group by u")
	BigDecimal obterSaldoPorTipoLancamentoUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);

}
