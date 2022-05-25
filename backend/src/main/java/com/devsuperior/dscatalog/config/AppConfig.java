package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Classe de configuração com @Configuration
 */
@Configuration
public class AppConfig {

    /**
     * Bean é um componente do Spring, com o @Bean o Spring Boot passa a gerenciar a execução do método.
     * Nesse caso esse objeto será utilizado para encriptar as senhas.
     * O simples fato de ter incluído a dependência do Spring Security, a aplicação fica protegida e
     * só podemos acessá-la com a senha que o Spring disponibiliza
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Idealmente isso seria colocado em uma classe e pasta especifica de segurança, são objetos capazes de ler, decodificar e criar um token
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey("MY-JWT-SECRET"); // chave do token registrada
        return tokenConverter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        // responsavel por acessar o token
        return new JwtTokenStore(accessTokenConverter());
    }

}
