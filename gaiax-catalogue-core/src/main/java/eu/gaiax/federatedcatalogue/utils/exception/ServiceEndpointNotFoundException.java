package eu.gaiax.federatedcatalogue.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class ServiceEndpointNotFoundException extends RuntimeException {

    public ServiceEndpointNotFoundException(String message) {
        super(message);
    }
}
