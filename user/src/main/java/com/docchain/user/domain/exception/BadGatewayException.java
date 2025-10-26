package com.docchain.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há erro no gateway ou serviço downstream.
 * 
 * MOTIVO: Esta exceção é usada quando o serviço externo retorna erro 5xx
 * (erro interno do servidor). Diferente do ServiceUnavailable, indica que
 * houve um problema no processamento pelo serviço remoto, não apenas indisponibilidade.
 * Retorna HTTP 502 (Bad Gateway) para indicar que o problema está no serviço downstream.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class BadGatewayException extends RuntimeException {
    
    public BadGatewayException(String message) {
        super(message);
    }
    
    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
