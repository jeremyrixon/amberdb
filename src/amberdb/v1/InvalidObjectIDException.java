package amberdb.v1;

public class InvalidObjectIDException extends RuntimeException {

    private static final long serialVersionUID = 3967425361650030964L;

    public InvalidObjectIDException(String string) {
        super(string);
    }

}
