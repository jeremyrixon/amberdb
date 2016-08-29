package amberdb.v2;

public class DataIntegrityException extends RuntimeException {

    public DataIntegrityException() {
        super();
    }

    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
