package br.com.sherpainvest.financas.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.sherpainvest.financas.service.JwtService;
import br.com.sherpainvest.financas.service.impl.SecurityUserDetailsService;

public class JwtTokenFilter extends OncePerRequestFilter {
	
	private JwtService jwtService;
	
	private SecurityUserDetailsService userDetailsService;
	
	public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		
		if (authorization != null && authorization.startsWith("Bearer")) {
			String token = authorization.split(" ")[1];
			boolean isTokenValid = jwtService.isTokenValido(token);
			
			if (isTokenValid) {
				String login = jwtService.obterLoginUsuario(token);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(login);
				
				UsernamePasswordAuthenticationToken userAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				userAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(userAuthToken);				
			}
		}
			
		filterChain.doFilter(request, response);
	}

}
