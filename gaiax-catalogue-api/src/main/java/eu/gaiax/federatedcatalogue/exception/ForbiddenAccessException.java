package eu.gaiax.federatedcatalogue.exception;

public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException() {
    }

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
