package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Para fazer as consultas com JPQL temos que dar um apelido para os objetos que serao buscados, nessa caso demos obj
    // Com JPQL temos que informar o nome da classe que estamos buscando, nao da tabela
    // ":category" referencia o parametro que esta sendo passado no metodo
    @Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE " +
            "(:category IS NULL OR :category IN cats)")
    Page<Product> find(Category category, Pageable pageable);
}
