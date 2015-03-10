package amberdb.model.builder;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class Document {
    final JsonNode document;
    final ObjectMapper mapper = new ObjectMapper();
    
    public Document(JsonNode structure, JsonNode content) {
        this.document = mapper.createObjectNode();
        ((ObjectNode) document).put("structure", structure);
        ((ObjectNode) document).put("content", content);
    }
    
    public JsonNode getStructure() {
        return document.get("structure");
    }
    
    public JsonNode getContent() {
        return document.get("content");
    }
    
    public JsonNode getReport() {
        return document.get("report");
    }
    
    public void setStatusReport(JsonNode report) {
        ((ObjectNode) document).put("report", report);
    }
    
    public String toJson() throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    }
}
