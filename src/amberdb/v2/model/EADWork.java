package amberdb.v2.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class EADWork extends Work {

    @Column
    private String repository;
    @Column
    private String arrangement;
    @Column
    private String collectionNumber;
    @Column
    private String relatedMaterial;
    @Column
    private String folder;
    @Column
    private String copyingPublishing;
    @Column
    private String access;
    @Column
    private String restrictionsOnAccess;
    @Column
    private String scopeContent;
    @Column
    private String eadUpdateReviewRequired;
    @Column
    private Boolean australianContent;
    @Column
    private String bibliography;
    @Column
    private String provenance;
    @Column
    private String dateRangeInAS;
    @Column
    private String preferredCitation;
    
    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getArrangement() {
        return arrangement;
    }

    public void setArrangement(String arrangement) {
        this.arrangement = arrangement;
    }

    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public String getRelatedMaterial() {
        return relatedMaterial;
    }

    public void setRelatedMaterial(String relatedMaterial) {
        this.relatedMaterial = relatedMaterial;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getCopyingPublishing() {
        return copyingPublishing;
    }

    public void setCopyingPublishing(String copyingPublishing) {
        this.copyingPublishing = copyingPublishing;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRestrictionsOnAccess() {
        return restrictionsOnAccess;
    }

    public void setRestrictionsOnAccess(String restrictionsOnAccess) {
        this.restrictionsOnAccess = restrictionsOnAccess;
    }

    public String getScopeContent() {
        return scopeContent;
    }

    public void setScopeContent(String scopeContent) {
        this.scopeContent = scopeContent;
    }

    public String getEadUpdateReviewRequired() {
        return eadUpdateReviewRequired;
    }

    public void setEadUpdateReviewRequired(String eadUpdateReviewRequired) {
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }


    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getDateRangeInAS() {
        return dateRangeInAS;
    }

    public void setDateRangeInAS(String dateRangeInAS) {
        this.dateRangeInAS = dateRangeInAS;
    }

    public String getPreferredCitation() {
        return preferredCitation;
    }

    public void setPreferredCitation(String preferredCitation) {
        this.preferredCitation = preferredCitation;
    }

}
