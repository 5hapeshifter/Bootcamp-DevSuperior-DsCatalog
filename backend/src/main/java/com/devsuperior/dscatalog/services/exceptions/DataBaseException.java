package com.devsuperior.dscatalog.services.exceptions;

public class DataBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataBaseException(String msg) {
        super(msg); // dessa forma estamos repassando a msg para a classe superior
    }

}
