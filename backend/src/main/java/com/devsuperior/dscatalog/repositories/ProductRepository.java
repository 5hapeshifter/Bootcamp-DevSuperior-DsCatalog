package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Para fazer as consultas com JPQL temos que dar um apelido para os objetos que serao buscados, nessa caso demos obj
    // Com JPQL temos que informar o nome da classe que estamos buscando, nao da tabela
    // ":category" referencia o parametro que esta sendo passado no metodo
    @Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE " +
            "(COALESCE(:categories) IS NULL OR cats IN :categories) AND" +
            "(:name = '' OR LOWER(obj.name) LIKE LOWER (CONCAT('%', :name, '%')))")
    Page<Product> find(List<Category> categories, String name, Pageable pageable);

    // Consulta auxiliar para buscar as categorias dos produtos resolvendo o problema do N+1 consultas
    // Join Fetch busca o produto com as categorias e ele não funciona com pagina, por isso estamos retornando uma lista
    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
    List<Product> findProductsWithCategories(List<Product> products);
}
