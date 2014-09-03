package amberdb.facade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.Work;

public class EADWork {
    private final Work work;
    
    public EADWork(Work work) {
        this.work = work;
    }
    
    /**
     * Property: parent work
     */
    public void setParent(final EADWork parent) {
        work.setParent(parent.getProtectedWork());
    }
    
    /**
     * Property: parent work
     */
    public EADWork getParent() {
        return new EADWork(work.getParent());
    }
    
    public EADWork addPart() {
        Work part = (Work) work.addSection();
        return new EADWork(part);
    }
    
    public EADCopy addEADCopy(Path source, String mimeType) throws IOException {
        Copy eadCopy = work.addCopy();
        eadCopy.setCopyRole(CopyRole.FINDING_AID_COPY.code());
        eadCopy.addFile(source, mimeType);
        return new EADCopy(eadCopy);
    }
    
    /**
     * Property: collection
     */
    public void setCollection(String collection) {
        work.setCollection(collection);
    }
    
    /**
     * Property: collection
     */
    public String getCollection() {
        return work.getCollection();
    }
    
    /**
     * Property: title
     */
    public void setTitle(String title) {
        work.setTitle(title);
    }
    
    /**
     * Property: title
     */
    public String getTitle() {
        return work.getTitle();
    }
    
    /**
     * Property: sortIndex
     */
    public void setSortIndex(String sortIndex) {
        work.setSortIndex(sortIndex); 
    }
    
    /**
     * Property: sortIndex
     */
    public String getSortIndex() {
        return work.getSortIndex();
    }
    
    /**
     * Property: subUnitType
     */
    public void setSubUnitType(String subUnitType) {
        work.setSubUnitType(subUnitType);
    }
    
    /**
     * Property: subUnitType
     */
    public String getSubUnitType() {
        return work.getSubUnitType();
    }
    
    /**
     * Property: subUnitNo
     */
    public void setSubUnitNo(String subUnitNo) {
        work.setSubUnitNo(subUnitNo);
    }
    
    /**
     * Property: subUnitNo
     */
    public String getSubUnitNo() {
        return work.getSubUnitNo();
    }
    
    /**
     * Property: bibLevel
     */
    public void setBibLevel(String bibLevel) {
        work.setBibLevel(bibLevel);
    }
    
    /**
     * Property: bibLevel
     */
    public String getBibLevel() {
        return work.getBibLevel();
    }
    
    /**
     * Property: digitalStatus
     */
    public void setDigitalStatus(String digitalStatus) {
        work.setDigitalStatus(digitalStatus);
    }
    
    /**
     * Property: digitalStatus
     */
    public String getDigitalStatus() {
        return work.getDigitalStatus();
    }
    
    /**
     * Property: creator
     */
    public void setCreator(String creator) {
        work.setCreator(creator);
    }
    
    /**
     * Property: creator
     */
    public String getCreator() {
        return work.getCreator();
    }
    
    /**
     * Property: startDate
     */
    public void setStartDate(Date startDate) {
        work.setStartDate(startDate);
    }
    
    /**
     * Property: startDate
     */
    public Date getStartDate() {
        return work.getStartDate();
    }
    
    /**
     * Property: endDate
     */
    public void setEndDate(Date endDate) {
        work.setEndDate(endDate);
    }
    
    /**
     * Property: endDate
     */
    public Date getEndDate() {
        return work.getEndDate();
    }
    
    /**
     * Property: extent
     */
    public void setExtent(String extent) {
        work.setExtent(extent);
    }
    
    /**
     * Property: extent
     */
    public String getExtent() {
        return work.getExtent();
    }
    
    /**
     * Property: childRange
     */
    public void setChildRange(String childRange) {
        work.setChildRange(childRange);
    }
    
    /**
     * Property: childRange
     */
    public String getChildRange() {
        return work.getChildRange();
    }
    
    /**
     * Property: startChild
     */
    public void setStartChild(String startChild) {
        work.setStartChild(startChild);
    }
    
    /**
     * Property: startChild
     */
    public String getStartChild() {
        return work.getStartChild();
    }
    
    /**
     * Property: endChild
     */
    public void setEndChild(String endChild) {
        work.setEndChild(endChild);
    }
    
    /**
     * Property: endChild
     */
    public String getEndChild() {
        return work.getEndChild();
    }
    
    /**
     * Property: recordSource
     */
    public void setRecordSource(String recordSource) {
        work.setRecordSource(recordSource);
    }
    
    /**
     * Property: recordSource
     */
    public String getRecordSource() {
        return work.getRecordSource();
    }
    
    /**
     * Property: localSystemNumber
     */
    public void setLocalSystemNumber(String localSystemNumber) {
        work.setLocalSystemNumber(localSystemNumber);
    }
    
    /**
     * Property: localSystemNumber
     */
    public String getLocalSystemNumber() {
        return work.getLocalSystemNumber();
    }
    
    /**
     * Property: accessConditions
     */
    public void setAccessConditions(String accessConditions) {
        work.setAccessConditions(accessConditions);
    }
    
    /**
     * Property: accessConditions
     */
    public String getAccessConditions() {
        return work.getAccessConditions();
    }
    
    /**
     * Property: constraint
     */
    public void setConstraint(List<String> constraint) throws JsonParseException, JsonMappingException, IOException {
        work.setConstraint(constraint);
    }
    
    /**
     * Property: constraint
     */
    public List<String> getConstraint() throws JsonParseException, JsonMappingException, IOException {
        return work.getConstraint();
    }
    
    /**
     * Property: rdsAcknowledgementType
     */
    public void setRdsAcknowledgementType(String rdsAcknowledgementType) {
        work.setRdsAcknowledgementType(rdsAcknowledgementType);
    }
    
    /**
     * Property: rdsAcknowledgementType
     */
    public String getRdsAcknowledgementType() {
        return work.getRdsAcknowledgementType();
    }
    
    /**
     * Property: rdsAcknowledgementReceiver
     */
    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver) {
        work.setRdsAcknowledgementReceiver(rdsAcknowledgementReceiver);
    }
    
    /**
     * Property: rdsAcknowledgementReceiver
     */
    public String getRdsAcknowledgementReceiver() {
        return work.getRdsAcknowledgementReceiver();
    }
    
    /**
     * Property: eadUpdateReviewRequired
     */
    public void setEADUpdateReviewRequired(String eadUpdateReviewRequired) {
        work.setEADUpdateReviewRequired(eadUpdateReviewRequired);
    }
    
    /**
     * Property: eadUpdateReviewRequired
     */
    public String getEADUpdateReviewRequired() {
        return work.getEADUpdateReviewRequired();
    }
    
    /**
     * Property: folder
     */
    public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException {
        work.setFolder(folder);
    }
    
    /**
     * Property: folder
     */
    public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException {
        return work.getFolder();
    }
    
    protected Work getProtectedWork() {
        return work;
    }
}
