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

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

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

    /*
        private ProductRepository repository2;
    */
    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO dto;
    private Long categoryId;
    //private Product dtoToProduct;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L; // Utilizamos quando tentamos deletar um objeto que depende do outro
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        dto = new ProductDTO(1L, "AAAA", "BBBBB", 5.00, "www.naoxiste.com", Instant.now());
        categoryId = 0L;

        /*
         Como o método retorna um pageable, temos que fazer um cast para Pageable para o compilador saber o tipo do objeto
         O ArgumentMatchers passa qualquer argumento que satisfaça a condição do objeto
         */
        Mockito.when(repository.findAll((Pageable) any())).thenReturn(page);
        Mockito.when(repository.save(any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.find(any(), any(),any())).thenReturn(page);
        Mockito.when(repository.getOne(existingId)).thenReturn(product);

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
        Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);

    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExist() {
        ProductDTO productDTO = service.update(existingId, dto);
        Assertions.assertNotNull(productDTO);
        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class,() ->service.update(nonExistingId, dto));
        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExist() {
        ProductDTO productDTO = service.findById(existingId);
        Assertions.assertNotNull(productDTO);
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
        Mockito.verify(repository).findById(nonExistingId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);
        Page<ProductDTO> result = service.findAllPaged(categoryId, "", pageable);
        Assertions.assertNotNull(result);
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependentIdExist() {
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
