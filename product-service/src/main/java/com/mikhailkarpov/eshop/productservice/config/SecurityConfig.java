package com.mikhailkarpov.eshop.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import static org.springframework.http.HttpMethod.*;

@Validated
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @NotBlank(message = "JWT issuer uri must be provided")
    @Value("${app.security.oauth2.jwt.issuer-uri}")
    private String issuerUri;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //@formatter:off
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers(POST, "/categories/**").hasAuthority("SCOPE_category")
                .antMatchers(PUT, "/categories/**").hasAuthority("SCOPE_category")
                .antMatchers(DELETE, "/categories/**").hasAuthority("SCOPE_category")
                .antMatchers(POST, "/products/**").hasAuthority("SCOPE_product")
                .antMatchers(PUT, "/products/**").hasAuthority("SCOPE_product")
                .antMatchers(DELETE, "/products/**").hasAuthority("SCOPE_product")
                .anyRequest().authenticated().and()
            .exceptionHandling()
                .authenticationEntryPoint(((request, response, ex) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()))
                ).and()
            .oauth2ResourceServer().jwt();
        //@formatter:on
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
}
