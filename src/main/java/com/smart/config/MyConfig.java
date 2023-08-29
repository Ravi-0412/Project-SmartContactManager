package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import com.smart.dao.UserRepository;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	@Autowired
	private UserRepository userRepository;

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	  return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//	}
	
	
	@Bean
	public DaoAuthenticationProvider authProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}



	@SuppressWarnings({ "deprecation", "removal" })
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/**").permitAll().and().formLogin()
		.loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
		.and().csrf().disable();
		
		
		http.authenticationProvider(authProvider()); // of 'DaoAuthenticationProvider'.
		
		DefaultSecurityFilterChain defaultSecurityFilterChain  = http.build();
		return defaultSecurityFilterChain;
	}

}
