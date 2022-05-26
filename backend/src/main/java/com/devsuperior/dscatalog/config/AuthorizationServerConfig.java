package com.devsuperior.dscatalog.config;

import com.devsuperior.dscatalog.components.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer // Anotação para representar o authorizationServer do Oauth
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value(value = "${security.oauth2.client.client-id}")
    private String clientId;

    @Value(value = "${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value(value = "${jwt.duration}")
    private Integer jwtDuration;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    private JwtTokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenEnhancer tokenEnhancer;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // estamos inserindo o nome do metodo
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    // Aqui estamos definindo como sera a nossa autenticacao e quais serao os dados do cliente, as configuracoes da aplicacao
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId) // aqui é a identificação do clientId, o front end deve informar esse dado identico para acessar a nossa aplicacao
                .secret(passwordEncoder.encode(clientSecret)) // senha da aplicacao
                .scopes("read", "write") // permissões de acesso
                .authorizedGrantTypes("password")
                .accessTokenValiditySeconds(jwtDuration); // tempo de um dia de validade do token
    }

    // Metodo responsavel por gerenciar a autorizacao e o formato do token
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        // adicionando informacoes adicionais no token
        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer));

        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                .tokenEnhancer(chain);
    }
}
