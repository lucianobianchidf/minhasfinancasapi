package br.com.sherpainvest.financas.api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LancamentoDTO {
	
	private Long id;
	
	private String descricao;
	
	private Integer mes;
	
	private Integer ano;
	
	private BigDecimal valor;
	
	private Long idUsuario;
	
	private String tipoLancamento;
	
	private String statusLancamento;

}
