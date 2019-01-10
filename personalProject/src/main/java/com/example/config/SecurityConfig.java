package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.example.config.CsrfHeaderFilter;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
@EnableWebSecurity // CsrfToken 자동포함 (타임리프일 폼)
@PropertySource({"classpath:application.properties", "classpath:database.properties"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
    	return new CustomAuthenticationSuccessHandler();
    }
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }
    
    @Bean 
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, SpringSecurityDialect sec) { 
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine(); 
        templateEngine.setTemplateResolver(templateResolver); 
        templateEngine.addDialect(sec); // Enable use of "sec" 
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine; 
    }

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers(HttpMethod.POST, "/portfolio/post");
    }
    
    protected void configure(HttpSecurity http) throws Exception {
    	http.authorizeRequests()
				.antMatchers("/**").permitAll()
//				.antMatchers(HttpMethod.POST, "/spring_security_login").hasRole("ANONYMOUS")
//				.antMatchers(HttpMethod.DELETE, "/board/post/**").hasRole("BASIC")
    			.antMatchers(
    					"/"
    					, "/assets/**"
    					, "/contactform/**"
    					, "/board"
    					, "/portfolio"
    					).permitAll()
    			.antMatchers(
    					"/login"
    					, "/login-error"
    					, "/regist/post").hasRole("ANONYMOUS")
    			.antMatchers("/board/post").hasRole("BASIC")
    			.antMatchers("/portfolio/post").hasRole("BASIC")
				.anyRequest().authenticated()
//    			.antMatchers("/login","/login-error","/regist/post").access("hasRole('ROLE_BASIC')")//.access("isAnonymous()")//hasRole("ADMIN")//.access("hasRole('ROLE_ANONYMOUS')")//duplicationLoginDisable//.hasAuthority("ROLE_ANONYMOUS")//.access("anonymous() and !isAuthenticated()")
//    	        .antMatchers("/", "/entries","/signup").permitAll()
//                .antMatchers("/**").permitAll()
            .and()
            	.exceptionHandling().accessDeniedPage("/") //접근제한 에러페이지 이동
    		.and()
    			.formLogin()
		    		.loginPage("/login")
		        	.loginProcessingUrl("/spring_security_login")
		        	.usernameParameter("email")
		        	.passwordParameter("pwd")
//		        	.successHandler(authenticationSuccessHandler())
//		            .defaultSuccessUrl("/successLogin",true).permitAll() 
	                .failureUrl("/login-error")
//	            	.failureHandler(authenticationFailureHandler())
//	            	.failureUrl("/403")
		    		.permitAll()
	    	.and()
	    		.logout()
		        	.logoutUrl("/logout")
		        	.logoutSuccessUrl("/")
		        	.permitAll()
		    .and()
	        	.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
	        	.csrf().csrfTokenRepository(csrfTokenRepository()).disable()
	        	.headers()
	        	.frameOptions()
	        	.disable();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver= new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("utf-8");
		commonsMultipartResolver.setMaxUploadSize(10485760);
		return commonsMultipartResolver;
	}
}
