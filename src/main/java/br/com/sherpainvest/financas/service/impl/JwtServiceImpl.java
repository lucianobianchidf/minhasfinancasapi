package br.com.sherpainvest.financas.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.sherpainvest.financas.model.entity.Usuario;
import br.com.sherpainvest.financas.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {
	
	@Value("${jwt.expiracao}")
	private String expiracao;
	
	@Value("${jwt.chave-assinatura}")
	private String chaveAssinatura;

	@Override
	public String gerarToken(Usuario usuario) {
		long expLong = Long.valueOf(expiracao);
		LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expLong);
		
		Date data = Date.from(dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant());
		String horaExpiracao = dataHoraExpiracao.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
		
		String token = Jwts.builder()
						.setExpiration(data)
						.setSubject(usuario.getEmail())
						.claim("nome", usuario.getNome())
						.claim("userid", usuario.getId())
						.claim("horaExpiracao", horaExpiracao)
						.signWith(SignatureAlgorithm.HS512, chaveAssinatura)
						.compact();
		
		return token;	
	}

	@Override
	public Claims obterClaims(String token) throws ExpiredJwtException {
		return Jwts.parser()
					.setSigningKey(chaveAssinatura)
					.parseClaimsJws(token)
					.getBody();
	}

	@Override
	public boolean isTokenValido(String token) {
		try {
			Claims claims = obterClaims(token);
			Date dataExpiracao = claims.getExpiration();
			
			LocalDateTime dataHoraExpiracao = dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			
			boolean dataHoraAnteriorDataExpiracao = LocalDateTime.now().isBefore(dataHoraExpiracao);
			return dataHoraAnteriorDataExpiracao;
		}catch (ExpiredJwtException e) {
			return false;
		}
	}

	@Override
	public String obterLoginUsuario(String token) {
		Claims claims = obterClaims(token);
		return claims.getSubject();
	}

}
