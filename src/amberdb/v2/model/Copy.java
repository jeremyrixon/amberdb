package amberdb.v2.model;

import amberdb.v2.model.mapper.CopyMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(CopyMapper.class)
public class Copy extends Node {

    private Date dcmDateTimeUpdated;
    private String extent;
    private String dcmRecordUpdater;
    private String localSystemNumber;
    private String encodingLevel;
    private String standardId;
    private String language;
    private String title;
    private String holdingId;
    private String internalAccessConditions;
    private Boolean australianContent;
    private Date dateCreated;
    private String contributor;
    private String timedStatus;
    private String copyType;
    private String alias;
    private String copyStatus;
    private String copyRole;
    private String manipulation;
    private String recordSource;
    private String algorithm;
    private String bibId;
    private String creator;
    private String otherNumbers;
    private Date dcmDateTimeCreated;
    private String materialType;
    private String commentsExternal;
    private String coordinates;
    private String creatorStatement;
    private String classification;
    private String currentVersion;
    private String commentsInternal;
    private String bestCopy;
    private String carrier;
    private String holdingNumber;
    private String series;
    private String publisher;
    private String dcmRecordCreator;
    private String dcmCopyPid;

    public Copy(int id, int txn_start, int txn_end, Date dcmDateTimeUpdated, String extent, String dcmRecordUpdater,
                String localSystemNumber, String encodingLevel, String standardId, String language, String title,
                String holdingId, String internalAccessConditions, Boolean australianContent, Date dateCreated,
                String contributor, String timedStatus, String copyType, String alias, String copyStatus,
                String copyRole, String manipulation, String recordSource, String algorithm, String bibId,
                String creator, String otherNumbers, Date dcmDateTimeCreated, String materialType,
                String commentsExternal, String coordinates, String creatorStatement, String classification,
                String currentVersion, String commentsInternal, String bestCopy, String carrier, String holdingNumber,
                String series, String publisher, String dcmRecordCreator, String dcmCopyPid) {
        super(id, txn_start, txn_end);
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
        this.extent = extent;
        this.dcmRecordUpdater = dcmRecordUpdater;
        this.localSystemNumber = localSystemNumber;
        this.encodingLevel = encodingLevel;
        this.standardId = standardId;
        this.language = language;
        this.title = title;
        this.holdingId = holdingId;
        this.internalAccessConditions = internalAccessConditions;
        this.australianContent = australianContent;
        this.dateCreated = dateCreated;
        this.contributor = contributor;
        this.timedStatus = timedStatus;
        this.copyType = copyType;
        this.alias = alias;
        this.copyStatus = copyStatus;
        this.copyRole = copyRole;
        this.manipulation = manipulation;
        this.recordSource = recordSource;
        this.algorithm = algorithm;
        this.bibId = bibId;
        this.creator = creator;
        this.otherNumbers = otherNumbers;
        this.dcmDateTimeCreated = dcmDateTimeCreated;
        this.materialType = materialType;
        this.commentsExternal = commentsExternal;
        this.coordinates = coordinates;
        this.creatorStatement = creatorStatement;
        this.classification = classification;
        this.currentVersion = currentVersion;
        this.commentsInternal = commentsInternal;
        this.bestCopy = bestCopy;
        this.carrier = carrier;
        this.holdingNumber = holdingNumber;
        this.series = series;
        this.publisher = publisher;
        this.dcmRecordCreator = dcmRecordCreator;
        this.dcmCopyPid = dcmCopyPid;
    }
}
