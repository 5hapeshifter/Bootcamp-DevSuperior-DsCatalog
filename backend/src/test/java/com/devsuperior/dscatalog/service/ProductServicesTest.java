package com.devsuperior.dscatalog.service;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

/**
 * Testes Unitarios refere-se a testar uma classe específica sem carregar outras classes que ela depende, senao seria um teste integrado
 * O teste de unidade não acessa o banco de dados, temos que mudar o raciocínio para testar SOMENTE o service.
 * Por exemplo, testando o método 'delete' não vamos verificar se o objeto foi deletado, vamos testar somente a chamada do método, no caso
 * de o ID existir não acontecerá nada, mas se ele não existir teremos que tratar porque o service lança uma exceção.
 */
@ExtendWith(SpringExtension.class)
public class ProductServicesTest {

    @InjectMocks // Não podemos usar o Autowired em testes unitarios, senao os objetos das classes que dependemos seriam injetados
    private ProductService service;

    /*
     *  Utilizar quando não é necessário carregar o contexto da aplicação(@ExtendWith), mais enxuto e rápido que o @MockBean, ideal para teste de unidade ou unitário.
     *  Quando utilizamos o Mock temos que configurar o comportamento que deve ser simulado para qualquer método, da classe de serviços, como delete, save, update e etc.
     */
    @Mock
    private ProductRepository repository;

//    @MockBean // Utilizar quando uma classe carrega o contexto da aplicação e precisamos mockar algum bean do Spring(@WebMvcTest, SpringBootTest)
//    private ProductRepository repository2;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        /*
         Configuração dos comportamentos que devem ocorrer.
         Mockito.doNothing().when(repository).deleteById(existingid);
         Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(existingid);
         Devemos utilizar o 'when' depois da ação (doNothing, doThrow e etc) quando o retorno for void.
         Quando tiver um retorno usamos 'when' antes da ação.
        */
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        /*
         Como o método retorna um pageable, temos que fazer um cast para Pageable para o compilador saber o tipo do objeto
         O ArgumentMatchers passa qualquer argumento que satisfaça a condição do objeto
         */
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
        Assertions.assertThrows(DataBaseException.class, () -> {
            service.delete(dependentId);
        });
        // O método 'verify' perimite verificar se qualquer método da classe ou ‘interface’ foi chamado, nesse caso, deleteById.
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
        // O método 'verify' perimite verificar se qualquer método da classe ou ‘interface’ foi chamado, nesse caso, deleteById.
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
        // O método 'verify' perimite verificar se qualquer método da classe ou ‘interface’ foi chamado, nesse caso, deleteById.
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

}