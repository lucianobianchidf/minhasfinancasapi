package br.com.sherpainvest.financas.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sherpainvest.financas.api.dto.AtualizaStatusDTO;
import br.com.sherpainvest.financas.api.dto.LancamentoDTO;
import br.com.sherpainvest.financas.exception.RegraNegocioException;
import br.com.sherpainvest.financas.model.entity.Lancamento;
import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.model.enums.StatusLancamento;
import br.com.sherpainvest.financas.model.enums.TipoLancamento;
import br.com.sherpainvest.financas.service.LancamentoService;
import br.com.sherpainvest.financas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao, 
									@RequestParam(value = "mes", required = false) Integer mes, 
									@RequestParam(value = "ano", required = false) Integer ano,
									@RequestParam("usuario") Long idUsuario) {
		Lancamento lancamentoFiltro = Lancamento.builder()
										.descricao(descricao)
										.mes(mes)
										.ano(ano).build();
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if (!usuario.isPresent())
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
		else
			lancamentoFiltro.setUsuario(usuario.get());
		
	 	List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
	 	return ResponseEntity.ok(lancamentos);
	 }
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO) {
		try {
			Lancamento lancamento = converter(lancamentoDTO);
			lancamento = lancamentoService.salvar(lancamento);
			
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return lancamentoService.buscarPorId(id).map( lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO atualizaStatusDTO) {
		return lancamentoService.buscarPorId(id).map( entidade -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(atualizaStatusDTO.getStatus());
			
			if (statusSelecionado == null)
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento");
			try {
				entidade.setStatus(statusSelecionado);
				lancamentoService.atualizar(entidade);
				return new ResponseEntity(HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity<>("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{idLancamento}")
	public ResponseEntity atualizar(@PathVariable("idLancamento") Long idLancamento, @RequestBody LancamentoDTO lancamentoDTO) {
		return lancamentoService.buscarPorId(idLancamento).map(entidade -> {
			try {
				Lancamento lancamento = converter(lancamentoDTO);
				lancamento.setId(entidade.getId());
				lancamentoService.atualizar(lancamento);
				
				return new ResponseEntity(HttpStatus.OK);
				//return new ResponseEntity(lancamento, HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{idLancamento}")
	public ResponseEntity deletar(@PathVariable("idLancamento") Long idLancamento) {
		return lancamentoService.buscarPorId(idLancamento).map(entidade -> {
			lancamentoService.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.valor(lancamento.getValor())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.statusLancamento(lancamento.getStatus().name())
				.tipoLancamento(lancamento.getTipo().name())
				.idUsuario(lancamento.getUsuario().getId()).build();
	}
	
	private Lancamento converter(LancamentoDTO lancamentoDTO) {
		Lancamento lancamento = Lancamento.builder()
								.id(lancamentoDTO.getId())
								.descricao(lancamentoDTO.getDescricao())
								.ano(lancamentoDTO.getAno())
								.mes(lancamentoDTO.getMes())
								.valor(lancamentoDTO.getValor()).build();
		
		Usuario usuario = usuarioService.obterPorId(lancamentoDTO.getIdUsuario())
											.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado")); 
		
		lancamento.setUsuario(usuario);
		
		if (lancamentoDTO.getTipoLancamento() != null)
			lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipoLancamento()));
		
		if (lancamentoDTO.getStatusLancamento() != null)
			lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatusLancamento()));
		
		return lancamento;
	}

}
