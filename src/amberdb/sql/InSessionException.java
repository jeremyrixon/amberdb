package amberdb.sql;

public class InSessionException extends RuntimeException {

    public InSessionException() {
    }

    public InSessionException(String message) {
        super(message);
    }

    public InSessionException(Throwable cause) {
        super(cause);
    }

    public InSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
