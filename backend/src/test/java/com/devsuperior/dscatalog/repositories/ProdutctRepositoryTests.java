package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProdutctRepositoryTests {

    @Autowired
    private ProductRepository repository;


    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception { // O que for inserido dentro desse metodo, sera executado antes de cada teste da classe
        nonExistingId = 1000L;
        existingId = 1L;
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        long countTotalProducts = 25L;
        Product product = Factory.createProduct();
        product.setId(null);
        product = repository.save(product);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnAProductWhenIdExist() {
        Optional<Product> product = repository.findById(existingId);
        Assertions.assertNotNull(product.get());
    }

    @Test
    public void findByIdShouldNotReturnAProductWhenIdDoesNotExist() {
        Optional<Product> product = repository.findById(nonExistingId);
        Assertions.assertEquals(Optional.empty(), product);
    }
}
