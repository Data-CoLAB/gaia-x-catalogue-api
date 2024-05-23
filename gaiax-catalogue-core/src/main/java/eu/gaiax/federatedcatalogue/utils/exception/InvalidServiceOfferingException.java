package eu.gaiax.federatedcatalogue.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidServiceOfferingException extends RuntimeException {
    public InvalidServiceOfferingException(String message) {
        super(message);
    }
}
