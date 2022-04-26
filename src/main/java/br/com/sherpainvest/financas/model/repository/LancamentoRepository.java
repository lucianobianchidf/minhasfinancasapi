package br.com.sherpainvest.financas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sherpainvest.financas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> { 

}
