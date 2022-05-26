package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer // Classe responsavel por receber o token e verificar se o acesso da requisicao esta ok
public class RsourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private JwtTokenStore tokenStore;

    // rota que sera publica para acessar
    private static final String[] PUBLIC = {"/oauth/token"};

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

        http.authorizeRequests()
                .antMatchers(PUBLIC).permitAll()
                .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
                .antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") // essa rota so sera acessada com algum desses perfis
                .antMatchers(ADMIN).hasRole("ADMIN")
                .anyRequest().authenticated(); // aqui estamos exigindo que o usuario esteja logado para acessar qualquer outra rota

    }
}
