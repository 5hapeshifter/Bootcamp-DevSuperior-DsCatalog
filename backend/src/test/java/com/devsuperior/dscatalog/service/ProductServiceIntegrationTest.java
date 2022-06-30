//package com.devsuperior.dscatalog.service;
//
//import com.devsuperior.dscatalog.dto.ProductDTO;
//import com.devsuperior.dscatalog.repositories.ProductRepository;
//import com.devsuperior.dscatalog.services.ProductService;
//import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// *  Testes de integração são mais demorados porque temos que testar o contexto da aplicação e temos que testar
// *  a interação entre as camadas, DE SERVIÇO E REPOSITÓRIO NESSA CLASSE, por exemplo.
// */
//@SpringBootTest
//@Transactional // Anotação para fazer o rollback do banco de dados após cada método
//public class ProductServiceIntegrationTest {
//
//    /*
//        Como estamos no teste de integração podemos usar o Autowired
//     */
//    @Autowired
//    private ProductService service;
//
//    @Autowired
//    private ProductRepository repository;
//
//    private Long existingId;
//    private Long nonExistingId;
//    private Long countTotalProducts;
//    private Long categoryId;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        existingId = 1L;
//        nonExistingId = 1000L;
//        countTotalProducts = 25L;
//        categoryId = 1L;
//    }
//
//    @Test
//    public void deleteShouldDeleteResourcesWhenIdExist() {
//        service.delete(existingId);
//        Assertions.assertEquals(countTotalProducts - 1L, repository.count());
//    }
//
//    @Test
//    public void deleteShouldThrowResourceNotFoundExceptioWhenIdDoesNotExist() {
//        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            service.delete(nonExistingId);
//        });
//    }
//
//    @Test
//    public void findAllPagedShouldReturnPageWhenPage0Size10() {
//        // Estamos pedindo a pagina com 10 produtos, como temos 25, essa pagina tem que vir
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        Page<ProductDTO> result = service.findAllPaged(categoryId, pageRequest);
//
//        Assertions.assertFalse(result.isEmpty()); // verificando se a pagina esta vazia
//        Assertions.assertEquals(0, result.getNumber()); // verificando o numero da pagina
//        Assertions.assertEquals(10, result.getSize()); // verificando a quantidade de objetos
//        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
//    }
//
//    @Test
//    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
//        // Estamos pedindo a pagina com 10 produtos, como temos 25, essa pagina tem que vir
//        PageRequest pageRequest = PageRequest.of(50, 10);
//        Page<ProductDTO> result = service.findAllPaged(categoryId, pageRequest);
//
//        Assertions.assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void findAllPagedShouldReturnOrderedPageWhenSortByName() {
//        // Estamos pedindo a pagina com 10 produtos, como temos 25, essa pagina tem que vir
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
//        Page<ProductDTO> result = service.findAllPaged(categoryId, pageRequest);
//
//        Assertions.assertFalse(result.isEmpty());
//        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
//        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
//        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
//    }
//}
