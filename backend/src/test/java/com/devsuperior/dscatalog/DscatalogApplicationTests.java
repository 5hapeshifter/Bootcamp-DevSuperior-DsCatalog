package com.devsuperior.dscatalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest //Carrega o contexto da aplicação, utilizada mais em testes de integração
class DscatalogApplicationTests {

    /** Contexto na aplicação Spring corresponde a todos os componentes do Spring, a toda infraestrutura do Spring
     * isso impactaria muito nos testes, pois carregaria toda a estrutura para cada teste que fosse rodado e não
     * conseguiríamos testar individualmente uma classe, por exemplo
     */
    @Test
    void contextLoads() {
    }

}
