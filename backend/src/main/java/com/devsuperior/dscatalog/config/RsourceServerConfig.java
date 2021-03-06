package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableResourceServer // Classe responsavel por receber o token e verificar se o acesso da requisicao esta ok
public class RsourceServerConfig extends ResourceServerConfigurerAdapter {

    // a partir desse objeto conseguimos acessar diversas variaveis da aplicacao
    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenStore tokenStore;

    // rota que sera publica para acessar
    private static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"};

    // rota com nivel de acesso
    private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};

    // rota que sera acessada somente com nivel de administrador
    private static final String[] ADMIN = {"/users/**"};

    // configuracao do token store, passamos o nosso bean para ele ser decodificado e analisado, verificar a validade
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }

    // configuracao das rotas
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // configuracao para liberar o console do h2
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        http.authorizeRequests()
                .antMatchers(PUBLIC).permitAll()
                .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
                .antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") // essa rota so sera acessada com algum desses perfis
                .antMatchers(ADMIN).hasRole("ADMIN")
                .anyRequest().authenticated(); // aqui estamos exigindo que o usuario esteja logado para acessar qualquer outra rota

        // chamando a configuracao do cors
        http.cors().configurationSource(corsConfigurationSource());
    }

    // Configuracoes do CORS para que o seu backend seja acessado somente pela sua aplicacao
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*")); // Local para especificar a aplicacao que tem acesso, por ex: https://meudominio.com, asterisco libera tudo
        corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    // Estamos definindo que o cors fique registrado com a maxima precedencia
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean
                = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
