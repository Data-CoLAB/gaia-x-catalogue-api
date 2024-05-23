package eu.gaiax.federatedcatalogue.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class SelfDescriptionParseException extends RuntimeException {

    public SelfDescriptionParseException(String message) {
        super(message);
    }
}
