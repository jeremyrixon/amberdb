package amberdb.model.builder;

import java.util.Comparator;
import org.codehaus.jackson.JsonNode;

import amberdb.model.EADWork;

public class ComponentBuilder {
    private static final Comparator<JsonNode> COMPONENT_COMPARATOR = new Comparator<JsonNode>() {
        public int compare(JsonNode collection, JsonNode component) {
            // TODO:
            return -1;
        }
        
        public boolean equals(JsonNode comp) {
            // TODO:
            return false;
        }
    };
    
    enum MergeType {
        NEW_COMP(0), UPDATED_COMP(1), DELETED_COMP(2);
        
        private int code;
        
        private MergeType(int code) {
            this.code = code;
        }
        
        public int code() {
            return code;
        }
    }
    
    public static void mergeComponet(EADWork collection, JsonNode compoent) {
        // TODO
    }
}
