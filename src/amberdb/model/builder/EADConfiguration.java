package amberdb.model.builder;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.ArrayNode;

public class EADConfiguration {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode config;
    
    public JsonNode getConfig() {
        if (config != null) return config;
        config = mapper.createObjectNode();
        ((ObjectNode) config).put("collection", getCollectionCfg());
        return config;
    }
    
    protected JsonNode getCollectionCfg() {
        JsonNode collectionNode = mapper.createObjectNode();
        ((ObjectNode) collectionNode).put("cfgDoc", getCollectionCfgDoco());
        ((ObjectNode) collectionNode).put("validateXML", "no");
        ((ObjectNode) collectionNode).put("storeCopy", "yes");
        ((ObjectNode) collectionNode).put("applicable-attributes-to-all-fields", getApplicableAttrsToAllFldsCfg());
        ((ObjectNode) collectionNode).put("fields", getMappedCollectionFieldsCfg());
        ((ObjectNode) collectionNode).put("excludes", getExcludedElementsCfg());
        ((ObjectNode) collectionNode).put("sub-elements", getSubElementsCfg());
        return collectionNode;
    }
    
    protected String getCollectionCfgDoco() {
        String cfgDoc = "This configuration specify the rules and fields for "
                      + "parsing EAD file to create a collection of works "
                      + "under the assigned top-level work. "
                      + "note: the name for each fields within a \"field\":{...} config segment must be unqiue.";
        return cfgDoc;
    }
    protected JsonNode getApplicableAttrsToAllFldsCfg() {
        JsonNode applyAttrToAllFlds = mapper.createObjectNode();
        ((ObjectNode) applyAttrToAllFlds).put("access-attribute", "@audience");
        ((ObjectNode) applyAttrToAllFlds).put("render-attribute", "@render");
        return applyAttrToAllFlds;
    }
    
    protected JsonNode getMappedCollectionFieldsCfg() {
        JsonNode collectionFields = mapper.createObjectNode();
        ((ObjectNode) collectionFields).put("collection-name", "fileName");
        ((ObjectNode) collectionFields).put("summary", getMappedSummaryFieldsCfg());
        ((ObjectNode) collectionFields).put("introduction", getMappedIntroductionFieldsCfg());
        ((ObjectNode) collectionFields).put("bioghistory", getMappedBioghistoryFieldsCfg());
        ((ObjectNode) collectionFields).put("adminInfo", getMappedAdminInfoFieldsCfg());
        return collectionFields;
    }
    
    protected JsonNode getMappedSummaryFieldsCfg() {
        JsonNode summaryFields = mapper.createObjectNode();
        ((ObjectNode) summaryFields).put("uuid", "notsuuplied");
        ((ObjectNode) summaryFields).put("dcmpi", "//ead:ead/ead:archdesc/ead:did/ead:materialspec");
        ((ObjectNode) summaryFields).put("eadid", "//ead:ead/ead:eadheader/ead:eadid");
        ((ObjectNode) summaryFields).put("collection-number", "//ead:ead/ead:eadheader/ead:filedesc/ead:titlestmt/ead:titleproper/ead:num");
        ((ObjectNode) summaryFields).put("sponsor", "//ead:ead/ead:eadheader/ead:filedesc/ead:titlestmt/ead:sponsor");
        ((ObjectNode) summaryFields).put("creator", "//ead:ead/ead:archdesc/ead:did/ead:origination/ead:persname");
        ((ObjectNode) summaryFields).put("title", "//ead:ead/ead:archdesc/ead:did/ead:unittitle");
        ((ObjectNode) summaryFields).put("date-range", "//ead:ead/ead:archdesc/ead:did/ead:unitdate");
        ((ObjectNode) summaryFields).put("extent", "//ead:ead/ead:archdesc/ead:did/ead:physdesc/ead:extent");
        return summaryFields;
    }
    
    protected JsonNode getMappedIntroductionFieldsCfg() {
        JsonNode introductionFields = mapper.createObjectNode();
        ((ObjectNode) introductionFields).put("repository", "//ead:ead/ead:archdesc/ead:did/ead:repository/ead:corpname");
        ((ObjectNode) introductionFields).put("scope-n-content", "//ead:ead/ead:archdesc/ead:scopecontent/ead:p");
        ((ObjectNode) introductionFields).put("arrangement", "//ead:ead/ead:archdesc/ead:arrangement/ead:p");
        ((ObjectNode) introductionFields).put("provenance", "//ead:ead/ead:archdesc/ead:acqinfo");
        ((ObjectNode) introductionFields).put("copying-publishing", "//ead:ead/ead:archdesc/ead:userestrict");
        ((ObjectNode) introductionFields).put("preferred-citation", "//ead:ead/ead:archdesc/ead:prefercite");
        ((ObjectNode) introductionFields).put("related-material", "//ead:ead/ead:archdesc/ead:relatedmaterial");
        ((ObjectNode) introductionFields).put("separated-material", "//ead:ead/ead:archdesc/ead:separatedmaterial");
        return introductionFields;
    }
    
    protected JsonNode getMappedBioghistoryFieldsCfg() {
        JsonNode biogHistoryFields = mapper.createObjectNode();
        ((ObjectNode) biogHistoryFields).put("biographical-note", "//ead:ead/ead:archdesc/ead:bioghist");
        ((ObjectNode) biogHistoryFields).put("bibliography", "//ead:ead/ead:archdesc/ead:bibliography");
        return biogHistoryFields;
    }
    
    protected JsonNode getMappedAdminInfoFieldsCfg() {
        JsonNode adminInfoFields = mapper.createObjectNode();
        ((ObjectNode) adminInfoFields).put("publicationstmt", "//ead:ead/ead:eadheader/ead:filedesc/ead:publicationstmt");
        ((ObjectNode) adminInfoFields).put("revisiondesc", "//ead:ead/ead:eadheader/ead:revisiondesc");
        ((ObjectNode) adminInfoFields).put("accessrestrict", "//ead:ead/ead:archdesc/ead:accessrestrict");
        ((ObjectNode) adminInfoFields).put("userestrict", "//ead:ead/ead:archdesc/ead:userestrict");
        ((ObjectNode) adminInfoFields).put("custodhist", "//ead:ead/ead:archdesc/ead:custodhist");
        ((ObjectNode) adminInfoFields).put("accruals", "//ead:ead/ead:archdesc/ead:accruals");
        ((ObjectNode) adminInfoFields).put("acqinfo", "//ead:ead/ead:archdesc/ead:acqinfo");
        ((ObjectNode) adminInfoFields).put("processinfo", "//ead:ead/ead:archdesc/ead:processinfo");
        ((ObjectNode) adminInfoFields).put("appraisal", "//ead:ead/ead:archdesc/ead:appraisal");
        ((ObjectNode) adminInfoFields).put("altformavail", "//ead:ead/ead:archdesc/ead:altformavail");
        ((ObjectNode) adminInfoFields).put("originalsloc", "//ead:ead/ead:archdesc/ead:originalsloc");
        return adminInfoFields;
    }
    
    protected ArrayNode getExcludedElementsCfg() {
        ArrayNode excluded = mapper.createArrayNode();
        excluded.add("arc");
        excluded.add("archdescgrp");
        excluded.add("bibseries");
        excluded.add("change");
        excluded.add("creation");
        excluded.add("descgrp");
        excluded.add("descrules");
        excluded.add("div");
        excluded.add("dscgrp");
        excluded.add("eadgrp");
        excluded.add("eadid");
        excluded.add("frontmatter");
        excluded.add("language");
        excluded.add("notestmt");
        excluded.add("profiledesc");
        excluded.add("publicationstmt");
        excluded.add("revisiondesc");
        excluded.add("runner");
        excluded.add("seriesstmt");
        excluded.add("titlepage");
        return excluded;
    }
    
    protected JsonNode getSubElementsCfg() {
        JsonNode subElements = mapper.createObjectNode();
        ((ObjectNode) subElements).put("base", "//ead:ead/ead:archdesc/ead:dsc");
        JsonNode series = mapper.createObjectNode();
        ((ObjectNode) series).put("series", getMappedSubElementFieldsCfg());
        ((ObjectNode) subElements).put("fields", series);
        ((ObjectNode) subElements).put("repeatable-element", "ead:c");
        return subElements;
    }
    
    protected JsonNode getMappedSubElementFieldsCfg() {
        JsonNode mappedFields = mapper.createObjectNode();
        ((ObjectNode) mappedFields).put("dcmpi", "ead:did/ead:materialspec");
        ((ObjectNode) mappedFields).put("title", "ead:did/ead:unittitle");
        ((ObjectNode) mappedFields).put("extent", "ead:did/ead:physdesc/ead:extent");
        ((ObjectNode) mappedFields).put("date-range", "ead:did/ead:unitdate");
        ((ObjectNode) mappedFields).put("scope-n-content", "ead:scopecontent/ead:p");
        ((ObjectNode) mappedFields).put("container-number", "ead:did/ead:container");
        ((ObjectNode) mappedFields).put("container-label", "ead:did/ead:container/@label");
        ((ObjectNode) mappedFields).put("container-type", "ead:did/ead:container/@type");
        ((ObjectNode) mappedFields).put("container-uuid", "ead:did/ead:container/@id");
        ((ObjectNode) mappedFields).put("container-parent", "ead:did/ead:container/@parent");
        ((ObjectNode) mappedFields).put("component-level", "@level");
        ((ObjectNode) mappedFields).put("component-number", "ead:did/ead:unitid");
        ((ObjectNode) mappedFields).put("uuid", "@id");
        return mappedFields;
    }
}
