package com.dg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma despesa não é encontrada.
 * Esta exceção é mapeada para o status HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExpenseNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExpenseNotFoundException(String message) {
        super(message);
    }

    public ExpenseNotFoundException(Long id) {
        super("Despesa não encontrada com o ID: " + id);
    }

    public ExpenseNotFoundException(Long id, Long userId) {
        super("Despesa não encontrada com o ID: " + id + " para o usuário com ID: " + userId);
    }
}