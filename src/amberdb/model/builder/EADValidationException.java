package amberdb.model.builder;

public class EADValidationException extends RuntimeException {
    private static final long serialVersionUID = 3227464379300960108L;

    public EADValidationException(String string) {
        super(string);
    }
    
    public EADValidationException(String errorMsg, String...params) {
        super(String.format(errorMsg.replaceAll("\\$\\{.+?\\}", "%s"), params));
    }
}
