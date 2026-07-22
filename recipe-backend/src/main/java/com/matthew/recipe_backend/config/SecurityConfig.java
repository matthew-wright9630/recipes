package com.matthew.recipe_backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.matthew.recipe_backend.repositories.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserRepository userRepository;
	private final JwtConfig jwtConfig;

	public SecurityConfig(UserRepository userRepository, @Lazy JwtConfig jwtConfig) {
		this.userRepository = userRepository;
		this.jwtConfig = jwtConfig;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/actuator/health").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/recipes/publish").permitAll()
				.requestMatchers("/api/recipes/me").authenticated()
				.requestMatchers("/api/recipes/history").authenticated()
				.requestMatchers(HttpMethod.GET, "/api/recipes").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/recipes/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/recipes/*/pdf").permitAll()
				.requestMatchers("/uploads/**").permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
				.requestMatchers(
						"/",
						"/index.html",
						"/favicon.ico",
						"/*.js",
						"/*.css",
						"/assets/**")
				.permitAll()
				.requestMatchers("/error").permitAll()
				.anyRequest().authenticated())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtConfig, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, e) -> {
							System.out.println("UNAUTHENTICATED: " + request.getRequestURI());
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
						})
						.accessDeniedHandler((request, response, e) -> {
							System.out.println("ACCESS DENIED: " + request.getRequestURI());
							e.printStackTrace();
						}))
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return email -> userRepository.findByEmail(email.toLowerCase())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(
				List.of("http://localhost:4200", "http://52.21.113.119", "https://wrightrecipes.com/",
						"https://www.wrightrecipes.com/"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

}
