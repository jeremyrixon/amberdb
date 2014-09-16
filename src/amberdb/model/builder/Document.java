package amberdb.model.builder;

import org.codehaus.jackson.JsonNode;

public class Document {
    final JsonNode structure;
    final JsonNode index;
    
    public Document(JsonNode structure, JsonNode index) {
        this.structure = structure;
        this.index = index;
    }
    
    public JsonNode getStructure() {
        return structure;
    }
    
    public JsonNode getIndex() {
        return index;
    }
}
