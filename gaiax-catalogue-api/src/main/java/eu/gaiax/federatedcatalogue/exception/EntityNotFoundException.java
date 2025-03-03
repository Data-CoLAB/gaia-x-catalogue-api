package eu.gaiax.federatedcatalogue.exception;

/**
 * The type Entity not found exception.
 */
public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -6585952089825184194L;

    /**
     * Instantiates a new Entity not found exception.
     */
    public EntityNotFoundException() {
    }

    /**
     * Instantiates a new Entity not found exception.
     *
     * @param message the message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Entity not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Entity not found exception.
     *
     * @param cause the cause
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Entity not found exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public EntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
