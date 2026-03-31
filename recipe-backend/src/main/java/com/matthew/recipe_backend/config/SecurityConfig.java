package com.matthew.recipe_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		System.out.println("Test");
		UserDetails userA = User.withUsername("mwright")
				.password("{noop}hashed_password") // {noop} = no encoding, just for testing
				.roles("USER")
				.build();

		System.out.println(userA);

		UserDetails userB = User.withUsername("ljw")
				.password("{noop}hashed_password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(userA, userB);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/recipes/{recipeId}/directions").authenticated()
				.anyRequest().permitAll())
				.httpBasic(Customizer.withDefaults())
				.build();
	}

}
