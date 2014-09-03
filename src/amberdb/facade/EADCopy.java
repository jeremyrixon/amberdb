package amberdb.facade;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import amberdb.model.Copy;
import amberdb.model.File;

public class EADCopy {
    private final Copy copy; 

    public EADCopy(Copy copy) {
        this.copy = copy;
    }
    
    /**
     * Property: materialType
     */
    public void setMaterialType(String materialType) {
        copy.setMaterialType(materialType);
    }
    
    /**
     * Property: materialType
     */
    public String getMaterialType() {
        return copy.getMaterialType();
    }
    
    /**
     * Property: dateCreated
     */
    public void setDateCreated(Date dateCreated) {
        copy.setDateCreated(dateCreated);
    }
    
    /**
     * Property: dateCreated
     */
    public Date getDateCreated() {
        return copy.getDateCreated();
    }
    
    /**
     * Property: sourceCopy
     */
    public void setSourceCopy(EADCopy sourceCopy) {
        copy.setSourceCopy(sourceCopy.getProtectedCopy());
    }
    
    /**
     * Property: sourceCopy
     */
    public EADCopy getSourceCopy() {
        return new EADCopy(copy.getSourceCopy());
    }
    
    /**
     * Property: bestCopy
     */
    public void setBestCopy(String bestCopy) {
        copy.setBestCopy(bestCopy);
    }
    
    /**
     * Property: bestCopy
     */
    public String getBestCopy() {
        return copy.getBestCopy();
    }
    
    /**
     * Property: localSystemNumber
     */
    public void setLocalSystemNumber(String localSystemNumber) {
        copy.setLocalSystemNumber(localSystemNumber);
    }
    
    /**
     * Property: localSystemNumber
     */
    public String getLocalSystemNumber() {
        return copy.getLocalSystemNumber();
    }
    
    /**
     * Property: acquisitionStatus
     */
    public void setAcquisitionStatus(String acquisitionStatus) {
        copy.setAcquisitionStatus(acquisitionStatus);
    }
    
    /**
     * Property: acquistionStatus
     */
    public String getAcquisitionStatus() {
        return copy.getAcquisitionStatus();
    }
    
    /**
     * Property: aquisitionCategory
     */
    public void setAcquisitionCategory(String aquisitionCategory) {
        copy.setAcquisitionCategory(aquisitionCategory);
    }
    
    /**
     * Property: alias
     */
    public List<String> getAlias() throws JsonParseException, JsonMappingException, IOException {
        return copy.getAlias();
    }
    
    /**
     * Property: alias
     */
    public void setAlias(List<String> aliasPI) throws JsonParseException, JsonMappingException, IOException {
        copy.setAlias(aliasPI);
    }
    
    /**
     * Property: commentsInternal
     */
    public String getCommentsInternal() {
        return copy.getCommentsInternal();
    }
    
    /**
     * Property: commentsInternal
     */
    public void setCommentsInternal(String commentsInternal) {
        copy.setCommentsInternal(commentsInternal);
    }
    
    /**
     * Property: commentsExternal
     */
    public String getCommentsExternal() {
        return copy.getCommentsExternal();
    }
    
    /**
     * Property: commentsExternal
     */
    public void setCommentsExternal(String commentsExternal) {
        copy.setCommentsExternal(commentsExternal);
    }
    
    public File getEADFile() {
        Iterable<File> files = copy.getFiles();
        if (files == null || !files.iterator().hasNext()) return null;
        return files.iterator().next();
    }
    
    protected Copy getProtectedCopy() {
        return copy;
    }
}
