package amberdb.model.builder;

import java.util.HashMap;
import java.util.Map;

public class EADValidationException extends RuntimeException {
    private static final long serialVersionUID = 3227464379300960108L;
    private static Map<String, String> customMsgs;

    public EADValidationException(String string) {
        super(string);
    }
    
    public EADValidationException(String string, Throwable e) {
        super(string, e);
    }
    
    public EADValidationException(String errorCode, String...params) {
        super(String.format(getValidationMessages().get(errorCode).replaceAll("\\$\\{.+?\\}", "%s"), params));
    }
    
    public EADValidationException(String errorCode, Throwable e, String...params) {
        super(String.format(getValidationMessages().get(errorCode).replaceAll("\\$\\{.+?\\}", "%s"), params), e);
    }
    
    protected static void setValidationMessages(Map<String, String> msgs) {
        if (customMsgs == null) initMessages();
        for (String msg : msgs.keySet()) {
            customMsgs.put(msg, msgs.get(msg));
        }
    }
    
    protected static Map<String, String> getValidationMessages() {
        if (customMsgs == null) initMessages();
        return customMsgs;
    }
    
    protected static void initMessages() {
        // create default validation messages
        customMsgs = new HashMap<>();
        customMsgs.put("FAILED_TO_CREATE_CHILD_WORK", "Failed to create child works from ${workObjId}.xml for work ${workObjId}.");
        customMsgs.put("NO_UUID_FOR_CHILD_WORK", "No Archives Space ID specified for ${nth} component work under work ${workObjId} (Archives Space ID: ${componentWorkUUID}).");
        customMsgs.put("MISSING_CONTAINER_TYPE", "No container type specified for one of the containers for the component work ${componentWorkObjId} (Archives Space ID: ${componentWorkUUID})");
        customMsgs.put("FAILED_TO_DETERMINE_START_DATE", "Failed to extract start date for the component work ${componentWorkObjId} (Archives Space ID: ${componentWorkUUID})");
        customMsgs.put("FAILED_EXTRACT_DATE_RANGE", "Failed to extract date range for the component work ${componentWorkObjId} (Archives Space ID: ${componentWorkUUID})");
        customMsgs.put("FAILED_EXTRACT_ENTITIIES", "Failed to extract entities for the work ${workObjId}.");
        customMsgs.put("FAILED_EXTRACT_FEATURE", "Failed to extract ${featureType} for the work ${workObjId}.");
        customMsgs.put("FAILED_EXTRACT_BIBLIOGRAPHY", "Failed to map bibliography for the work ${workObjId}.");
        customMsgs.put("FAILED_EXTRACT_CONTAINERS", "Failed to map containers for the work ${componentWorkObjId}.");
    }
}
