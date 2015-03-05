package amberdb.model.builder;

public class EADValidationException extends RuntimeException {
    private static final long serialVersionUID = 3227464379300960108L;
    private String errorCode;
    private String[] params;

    public EADValidationException(String string) {
        super(string);
    }
    
    public EADValidationException(String string, Throwable e) {
        super(string, e);
    }
    
    public EADValidationException(String errorCode, String...params) {
        super(errorCode);
        this.errorCode = errorCode;
        this.params = params;
    }
    
    public EADValidationException(String errorCode, Throwable e, String...params) {
        super(errorCode, e);
        this.errorCode = errorCode;
        this.params = params;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String[] getParams() {
        return params;
    }
}
