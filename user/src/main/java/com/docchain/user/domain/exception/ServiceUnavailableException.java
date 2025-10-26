package com.docchain.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando o serviço externo está temporariamente indisponível.
 * 
 * MOTIVO: Esta exceção é usada para situações de timeout, circuit breaker aberto,
 * ou falhas de conexão que indicam que o serviço pode estar indisponível mas
 * pode se recuperar em breve. Retorna HTTP 503 (Service Unavailable) para indicar
 * ao cliente que ele pode tentar novamente mais tarde.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
    
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
