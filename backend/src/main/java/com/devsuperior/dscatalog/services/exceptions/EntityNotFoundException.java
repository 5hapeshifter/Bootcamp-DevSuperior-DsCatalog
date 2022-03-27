package com.devsuperior.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String msg) {
        super(msg); // dessa forma estamos repassando a msg para a classe superior
    }

}
