package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class EADWork extends Work {
    @Column
    private String rdsAcknowledgementType;
    @Column
    private String rdsAcknowledgementReceiver;
    @Column
    private String eadUpdateReviewRequired;
    @Column
    private String scopeContent;
    @Column(name="bibliography")
    private String jsonBibliography;
    @Column(name="arrangement")
    private String jsonArrangement;
    @Column(name="access")
    private String jsonAccess;
    @Column(name="copyingPublishing")
    private String jsonCopyingPublishing;
    @Column(name="preferredCitation")
    private String jsonPreferredCitation;
    @Column(name="relatedMaterial")
    private String jsonRelatedMaterial;
    @Column
    private String adminInfo;
    @Column
    private String correspondenceIndex;
    @Column(name="provenance")
    private String jsonProvenance;
    @Column
    private String altform;
    @Column
    private String dateRangeInAS;
    @Column
    private String repository;
    @Column
    private String collectionNumber;
    @Column(name="folder")
    private String jsonFolder;
    @Column(name="folderType")
    private String jsonFolderType;
    @Column(name="folderNumber")
    private String jsonFolderNumber;
    @Column
    private String correspondenceHeader;
    @Column
    private String correspondenceId;

    private SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public void setRdsAcknowledgementType(String rdsAcknowledgementType) {
        this.rdsAcknowledgementType = rdsAcknowledgementType;
    }

    public String getRdsAcknowledgementReceiver() {
        return rdsAcknowledgementReceiver;
    }

    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver) {
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
    }

    public String getEadUpdateReviewRequired() {
        return eadUpdateReviewRequired;
    }

    public void setEadUpdateReviewRequired(String eadUpdateReviewRequired) {
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
    }

    public String getScopeContent() {
        return scopeContent;
    }

    public void setScopeContent(String scopeContent) {
        this.scopeContent = scopeContent;
    }

    public String getJSONBibliography() {
        return jsonBibliography;
    }

    public void setJSONBibliography(String jsonBibliography) {
        this.jsonBibliography = jsonBibliography;
    }

    public String getJSONArrangement() {
        return jsonArrangement;
    }

    public void setJSONArrangement(String jsonArrangement) {
        this.jsonArrangement = jsonArrangement;
    }

    public String getJSONAccess() {
        return jsonAccess;
    }

    public void setJSONAccess(String jsonAccess) {
        this.jsonAccess = jsonAccess;
    }

    public String getJSONCopyingPublishing() {
        return jsonCopyingPublishing;
    }

    public void setJSONCopyingPublishing(String jsonCopyingPublishing) {
        this.jsonCopyingPublishing = jsonCopyingPublishing;
    }

    public String getJSONPreferredCitation() {
        return jsonPreferredCitation;
    }

    public void setJSONPreferredCitation(String jsonPreferredCitation) {
        this.jsonPreferredCitation = jsonPreferredCitation;
    }

    public String getJSONRelatedMaterial() {
        return jsonRelatedMaterial;
    }

    public void setJSONRelatedMaterial(String jsonRelatedMaterial) {
        this.jsonRelatedMaterial = jsonRelatedMaterial;
    }

    public String getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(String adminInfo) {
        this.adminInfo = adminInfo;
    }

    public String getCorrespondenceIndex() {
        return correspondenceIndex;
    }

    public void setCorrespondenceIndex(String correspondenceIndex) {
        this.correspondenceIndex = correspondenceIndex;
    }

    public String getJSONProvenance() {
        return jsonProvenance;
    }

    public void setJSONProvenance(String jsonProvenance) {
        this.jsonProvenance = jsonProvenance;
    }

    public String getAltform() {
        return altform;
    }

    public void setAltform(String altform) {
        this.altform = altform;
    }

    public String getDateRangeInAS() {
        return dateRangeInAS;
    }

    public void setDateRangeInAS(String dateRangeInAS) {
        this.dateRangeInAS = dateRangeInAS;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public String getJSONFolder() {
        return jsonFolder;
    }

    public void setJSONFolder(String jsonFolder) {
        this.jsonFolder = jsonFolder;
    }

    public String getJSONFolderType() {
        return jsonFolderType;
    }

    public void setJSONFolderType(String jsonFolderType) {
        this.jsonFolderType = jsonFolderType;
    }

    public String getJSONFolderNumber() {
        return jsonFolderNumber;
    }

    public void setJSONFolderNumber(String jsonFolderNumber) {
        this.jsonFolderNumber = jsonFolderNumber;
    }

    public String getCorrespondenceHeader() {
        return correspondenceHeader;
    }

    public void setCorrespondenceHeader(String correspondenceHeader) {
        this.correspondenceHeader = correspondenceHeader;
    }

    public String getCorrespondenceId() {
        return correspondenceId;
    }

    public void setCorrespondenceId(String correspondenceId) {
        this.correspondenceId = correspondenceId;
    }

    public void setBibliograph(List<String> bibliograph) throws JsonProcessingException {
        setJSONBibliography(serialiseToJSON(bibliograph));
    }

    public List<String> getBibliography() {
        return deserialiseJSONString(getJSONBibliography());
    }

    public void setArrangement(List<String> arrangement) throws JsonProcessingException {
        setJSONArrangement(serialiseToJSON(arrangement));
    }

    public List<String> getArrangement() {
        return deserialiseJSONString(getJSONArrangement());
    }

    public void setAccess(List<String> access) throws JsonProcessingException {
        setJSONAccess(serialiseToJSON(access));
    }

    public List<String> getAccess() {
        return deserialiseJSONString(getJSONAccess());
    }

    public void setCopyingPublishing(List<String> copyingPublishing) throws JsonProcessingException {
        setJSONCopyingPublishing(serialiseToJSON(copyingPublishing));
    }

    public List<String> getCopyingPublishing() {
        return deserialiseJSONString(getJSONCopyingPublishing());
    }

    public void setPreferredCitation(List<String> preferredCitation) throws JsonProcessingException {
        setJSONPreferredCitation(serialiseToJSON(preferredCitation));
    }

    public List<String> getPreferredCitation() {
        return deserialiseJSONString(getJSONPreferredCitation());
    }

    public void setRelatedMaterial(List<String> relatedMaterial) throws JsonProcessingException {
        setJSONRelatedMaterial(serialiseToJSON(relatedMaterial));
    }

    public List<String> getRelatedMaterial() {
        return deserialiseJSONString(getJSONRelatedMaterial());
    }

    public void setProvenance(List<String> provenance) throws JsonProcessingException {
        setJSONProvenance(serialiseToJSON(provenance));
    }

    public List<String> getProvenance() {
        return deserialiseJSONString(getJSONProvenance());
    }

    public void setFolder(List<String> folder) throws JsonProcessingException {
        setJSONFolder(serialiseToJSON(folder));
    }

    public List<String> getFolder() {
        return deserialiseJSONString(getJSONFolder());
    }

    public void setFolderType(List<String> folderType) throws JsonProcessingException {
        setJSONFolderType(serialiseToJSON(folderType));
    }

    public List<String> getFolderType() {
        return deserialiseJSONString(getJSONFolderType());
    }

    public void setFolderNumber(List<String> folderNumber) throws JsonProcessingException {
        setJSONFolderNumber(serialiseToJSON(folderNumber));
    }

    public List<String> getFolderNumber() {
        return deserialiseJSONString(getJSONFolderNumber());
    }

    public String getFmttedDateRange() {
        Date from = getStartDate();
        Date to = getEndDate();

        SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
        String fmttedFrom = (from == null)?"":dateFmt.format(from);
        String fmttedTo = (to == null)? "" : dateFmt.format(to);

        if (fmttedFrom.startsWith("01/01") && (fmttedFrom.endsWith("00:00:00") || fmttedFrom.endsWith("12:00:00"))) {
            fmttedFrom = yearFmt.format(from);
        }

        if (fmttedTo.startsWith("31/12") && (fmttedTo.endsWith("23:59:59") || fmttedTo.endsWith("12:00:00"))) {
            fmttedTo = yearFmt.format(to);
        }
        return fmttedFrom + " - " + fmttedTo;
    }

    public EADWork getEADWork(long objectId) {
        return workDao.getEADWork(objectId);
    }

    public EADWork checkEADWorkInCollectionByLocalSystemNumber(String localSystemNumber) {
        List<Work> worksInCollection = workDao.getWorksInCollection(localSystemNumber);

        if (worksInCollection != null) {
            Iterator it = worksInCollection.iterator();
            EADWork eadWork = (EADWork) it.next();
            if (eadWork.getParent() == this) {
                return eadWork;
            }
        }

        return null;
    }
}
