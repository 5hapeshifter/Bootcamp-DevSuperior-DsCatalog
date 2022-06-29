package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //QueryMethods - pega o padrão de escrita e converte na execucao, entao temos que concatenar o findBy + nomeDoAtributo
    //Mais indicado para consultas simples
    User findByEmail(String email);
    //QueryMethod para buscar uma lista de usuarios pelo primeiro nome
    List<User> findByFirstName(String firstName);

}
