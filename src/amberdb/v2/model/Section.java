package amberdb.v2.model;

import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.SectionMapper;

import java.util.Date;

@MapWith(SectionMapper.class)
public class Section extends Node {
    private String creator;
    private String accessConditions;
    private Boolean allowOnsiteAccess;
    private String abstractText;
    private String advertising;
    private String title;
    private String printedPageNumber;
    private String captions;
    private String internalAccessConditions;
    private String subUnitNo;
    private Date expiryDate;
    private String bibLevel;
    private Boolean illustrated;
    private String copyrightPolicy;
    private String metsId;
    private String subType;
    private String constraint1;

    public Section(int id, int txn_start, int txn_end, String creator, String accessConditions,
                   Boolean allowOnsiteAccess, String abstractText, String advertising, String title,
                   String printedPageNumber, String captions, String internalAccessConditions, String subUnitNo,
                   Date expiryDate, String bibLevel, Boolean illustrated, String copyrightPolicy, String metsId,
                   String subType, String constraint1) {
        super(id, txn_start, txn_end);
        this.creator = creator;
        this.accessConditions = accessConditions;
        this.allowOnsiteAccess = allowOnsiteAccess;
        this.abstractText = abstractText;
        this.advertising = advertising;
        this.title = title;
        this.printedPageNumber = printedPageNumber;
        this.captions = captions;
        this.internalAccessConditions = internalAccessConditions;
        this.subUnitNo = subUnitNo;
        this.expiryDate = expiryDate;
        this.bibLevel = bibLevel;
        this.illustrated = illustrated;
        this.copyrightPolicy = copyrightPolicy;
        this.metsId = metsId;
        this.subType = subType;
        this.constraint1 = constraint1;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }

    public Boolean getAllowOnsiteAccess() {
        return allowOnsiteAccess;
    }

    public void setAllowOnsiteAccess(Boolean allowOnsiteAccess) {
        this.allowOnsiteAccess = allowOnsiteAccess;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getAdvertising() {
        return advertising;
    }

    public void setAdvertising(String advertising) {
        this.advertising = advertising;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrintedPageNumber() {
        return printedPageNumber;
    }

    public void setPrintedPageNumber(String printedPageNumber) {
        this.printedPageNumber = printedPageNumber;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public void setInternalAccessConditions(String internalAccessConditions) {
        this.internalAccessConditions = internalAccessConditions;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public void setSubUnitNo(String subUnitNo) {
        this.subUnitNo = subUnitNo;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public void setBibLevel(String bibLevel) {
        this.bibLevel = bibLevel;
    }

    public Boolean getIllustrated() {
        return illustrated;
    }

    public void setIllustrated(Boolean illustrated) {
        this.illustrated = illustrated;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public void setCopyrightPolicy(String copyrightPolicy) {
        this.copyrightPolicy = copyrightPolicy;
    }

    public String getMetsId() {
        return metsId;
    }

    public void setMetsId(String metsId) {
        this.metsId = metsId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getConstraint1() {
        return constraint1;
    }

    public void setConstraint1(String constraint1) {
        this.constraint1 = constraint1;
    }
}
