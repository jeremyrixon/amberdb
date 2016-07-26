package amberdb.v2.model.dao;

import amberdb.v2.model.Work;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class WorkDao implements CrudDao<Work> {
    @Override
    @SqlQuery("select * from work where id = :id")
    public abstract Work get(@Bind("id") Long id);

    @Override
    @SqlUpdate("insert into work (" +
            "   id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, " +
            "   holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, " +
            "   classification , commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, " +
            "   dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, " +
            "   north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, " +
            "   moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, " +
            "   sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, " +
            "   interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, " +
            "   notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, " +
            "   allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, " +
            "   redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, " +
            "   otherTitle, imageServerUrl, localSystemno, acquisitionStatus, reorderType, immutable, copyrightPolicy, " +
            "   nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, " +
            "   representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, " +
            "   digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, " +
            "   alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, " +
            "   sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, " +
            "   dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) " +
            "VALUES (" +
            "   w:id, w:txn_start, w:txn_end, w:extent, w:dcmDateTimeUpdated, w:localSystemNumber, w:occupation, w:endDate, w:displayTitlePage, " +
            "   w:holdingId, w:hasRepresentation, w:totalDuration, w:dcmDateTimeCreated, w:firstPart, w:additionalTitle, w:dcmWorkPid, " +
            "   w:classification , w:commentsInternal, w:restrictionType, w:ilmsSentDateTime, w:subType, w:scaleEtc, w:startDate, " +
            "   w:dcmRecordUpdater, w:tilePosition, w:allowHighResdownload, w:south, w:restrictionsOnAccess, w:preservicaType, " +
            "   w:north, w:accessConditions, w:internalAccessConditions, w:eadUpdateReviewRequired, w:australianContent, " +
            "   w:moreIlmsDetailsRequired, w:rights, w:genre, w:deliveryUrl, w:recordSource, w:sheetCreationDate, w:creator, " +
            "   w:sheetName, w:coordinates, w:creatorStatement, w:additionalCreator, w:folderType, w:eventNote, " +
            "   w:interactiveIndexAvailable, w:startChild, w:bibLevel, w:holdingNumber, w:publicNotes, w:series, w:constraint1, " +
            "   w:notes, w:catalogueUrl, w:encodingLevel, w:materialFromMultipleSources, w:subject, w:sendToIlms, w:vendorId, " +
            "   w:allowOnsiteAccess, w:language, w:sensitiveMaterial, w:dcmAltPi, w:folderNumber, w:west, w:html, w:preservicaId, " +
            "   w:redocworksReason, w:workCreatedDuringMigration, w:author, w:commentsExternal, w:findingAidNote, w:collection, " +
            "   w:otherTitle, w:imageServerUrl, w:localSystemno, w:acquisitionStatus, w:reorderType, w:immutable, w:copyrightPolicy, " +
            "   w:nextStep, w:publisher, w:additionalSeries, w:tempHolding, w:sortIndex, w:isMissingPage, w:standardId, " +
            "   w:representativeId, w:edition, w:reorder, w:title, w:acquisitionCategory, w:subUnitNo, w:expiryDate, " +
            "   w:digitalStatusDate, w:east, w:contributor, w:publicationCategory, w:ingestJobId, w:subUnitType, w:uniformTitle, " +
            "   w:alias, w:rdsAcknowledgementType, w:issueDate, w:bibId, w:coverage, w:summary, w:additionalContributor, " +
            "   w:sendToIlmsDateTime, w:sensitiveReason, w:carrier, w:form, w:rdsAcknowledgementReceiver, w:digitalStatus, " +
            "   w:dcmRecordCreator, w:sprightlyUrl, w:depositType, w:parentConstraint)")
    public abstract Long insert(@Bind("w") Work work);

    @Override
    @SqlUpdate("update work set " +
            "txn_start = :w.txn_start, " +
            "txn_end = :w.txn_end, " +
            "extent = :w.extent, " +
            "dcmDateTimeUpdated = :w.dcmDateTimeUpdated, " +
            "localSystemNumber = :w.localSystemNumber, " +
            "occupation = :w.occupation, " +
            "endDate = :w.endDate, " +
            "displayTitlePage = :w.displayTitlePage, " +
            "holdingId = :w.holdingId, " +
            "hasRepresentation = :w.hasRepresentation, " +
            "totalDuration = :w.totalDuration, " +
            "dcmDateTimeCreated = :w.dcmDateTimeCreated, " +
            "firstPart = :w.firstPart, " +
            "additionalTitle = :w.additionalTitle, " +
            "dcmWorkPid = :w.dcmWorkPid, " +
            "classification = :w.classification, " +
            "commentsInternal = :w.commentsInternal, " +
            "restrictionType = :w.restrictionType, " +
            "ilmsSentDateTime = :w.ilmsSentDateTime, " +
            "subType = :w.subType, " +
            "scaleEtc = :w.subType, " +
            "startDate = :w.startDate, " +
            "dcmRecordUpdater = :w.dcmRecordUpdater, " +
            "tilePosition = :w.tilePosition, " +
            "allowHighResdownload = :w.allowHighResdownload, " +
            "south = :w.south, " +
            "restrictionsOnAccess = :w.restrictionsOnAccess, " +
            "preservicaType = :w.preservicaType, " +
            "north = :w.north, " +
            "accessConditions = :w.accessConditions, " +
            "internalAccessConditions = :w.internalAccessConditions, " +
            "eadUpdateReviewRequired = :w.eadUpdateReviewRequired, " +
            "australianContent = :w.australianContent, " +
            "moreIlmsDetailsRequired = :w.moreIlmsDetailsRequired, " +
            "rights = :w.rights, " +
            "genre = :w.genre, " +
            "deliveryUrl = :w.deliveryUrl, " +
            "recordSource = :w.recordSource, " +
            "sheetCreationDate = :w.sheetCreationDate, " +
            "creator = :w.creator, " +
            "sheetName = :w.sheetName, " +
            "coordinates = :w.coordinates, " +
            "creatorStatement = :w.creatorStatement, " +
            "additionalCreator = :w.additionalCreator, " +
            "folderType = :w.folderType, " +
            "eventNote = :w.eventNote, " +
            "interactiveIndexAvailable = :w.interactiveIndexAvailable, " +
            "startChild = :w.startChild, " +
            "bibLevel = :w.bibLevel, " +
            "holdingNumber = :w.holdingNumber, " +
            "publicNotes = :w.publicNotes, " +
            "series = :w.series, " +
            "constraint1 = :.constraint1, " +
            "notes = :w.notes, " +
            "catalogueUrl = :w.catalogueUrl, " +
            "encodingLevel = :w.encodingLevel, " +
            "materialFromMultipleSources = :w.materialFromMultipleSources, " +
            "subject = :w.subject, " +
            "sendToIlms = :w.sendToIlms, " +
            "vendorId = :w.vendorId, " +
            "allowOnsiteAccess = :w.allowOnsiteAccess, " +
            "language = :w.language, " +
            "sensitiveMaterial = :w.sensitiveMaterial, " +
            "dcmAltPi = :w.dcmAltPi, " +
            "folderNumber = :w.folderNumber, " +
            "west = :w.west, " +
            "html = :w.html, " +
            "preservicaId = :w.preservicaId, " +
            "redocworksReason = :w.redocworksReason, " +
            "workCreatedDuringMigration = :w.workCreatedDuringMigration, " +
            "author = :w.author, " +
            "commentsExternal = :w.commentsExternal, " +
            "findingAidNote = :w.findingAidNote, " +
            "collection = :w.collection, " +
            "otherTitle = :w.otherTitle, " +
            "imageServerUrl = :w.imageServerUrl, " +
            "localSystemno = :w.localSystemno, " +
            "acquisitionStatus = :w.acquisitionStatus, " +
            "reorderType = :w.reorderType, " +
            "immutable = :w.immutable, " +
            "copyrightPolicy = :w.copyrightPolicy, " +
            "nextStep = :w.nextStep, " +
            "publisher = :w.publisher, " +
            "additionalSeries = :w.additionalSeries, " +
            "tempHolding = :w.tempHolding, " +
            "sortIndex = :w.sortIndex, " +
            "isMissingPage = :w.isMissingPage, " +
            "standardId = :w.standardId, " +
            "representativeId = :w.representativeId, " +
            "edition = :w.edition, " +
            "reorder = :w.reorder, " +
            "title = :w.title, " +
            "acquisitionCategory = :w.acquisitionCategory, " +
            "subUnitNo = :w.subUnitNo, " +
            "expiryDate = :w.expiryDate, " +
            "digitalStatusDate = :w.digitalStatusDate, " +
            "east = :w.east, " +
            "contributor = :w.contributor, " +
            "publicationCategory = :w.publicationCategory, " +
            "ingestJobId = :w.ingestJobId, " +
            "subUnitType = :w.subUnitType, " +
            "uniformTitle = :w.uniformTitle, " +
            "alias = :w.alias, " +
            "rdsAcknowledgementType = :w.rdsAcknowledgementType, " +
            "issueDate = :w.issueDate, " +
            "bibId = :w.bibId, " +
            "coverage = :w.coverage, " +
            "summary = :w.summary, " +
            "additionalContributor = :w.additionalContributor, " +
            "sendToIlmsDateTime = :w.sendToIlmsDateTime, " +
            "sensitiveReason = :w.sensitiveReason, " +
            "carrier = :w.carrier, " +
            "form = :w.form, " +
            "rdsAcknowledgementReceiver = :w.rdsAcknowledgementReceiver, " +
            "digitalStatus = :w.digitalStatus, " +
            "dcmRecordCreator = :w.dcmRecordCreator, " +
            "sprightlyUrl = :w.sprightlyUrl, " +
            "depositType = :w.depositType, " +
            "parentConstraint = :w.parentConstraint " +
            "where id = :w.id")
    public abstract Work save(@BindBean("w") Work work);

    @Override
    @SqlUpdate("delete from work where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from work_history where id = :id")
    public abstract List<Work> getHistory(@Bind("id") Long id);

    @Override
    @SqlUpdate("insert into work_history (" +
            "   id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, " +
            "   holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, " +
            "   classification , commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, " +
            "   dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, " +
            "   north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, " +
            "   moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, " +
            "   sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, " +
            "   interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, " +
            "   notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, " +
            "   allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, " +
            "   redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, " +
            "   otherTitle, imageServerUrl, localSystemno, acquisitionStatus, reorderType, immutable, copyrightPolicy, " +
            "   nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, " +
            "   representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, " +
            "   digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, " +
            "   alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, " +
            "   sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, " +
            "   dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) " +
            "VALUES (" +
            "   w:id, w:txn_start, w:txn_end, w:extent, w:dcmDateTimeUpdated, w:localSystemNumber, w:occupation, w:endDate, w:displayTitlePage, " +
            "   w:holdingId, w:hasRepresentation, w:totalDuration, w:dcmDateTimeCreated, w:firstPart, w:additionalTitle, w:dcmWorkPid, " +
            "   w:classification , w:commentsInternal, w:restrictionType, w:ilmsSentDateTime, w:subType, w:scaleEtc, w:startDate, " +
            "   w:dcmRecordUpdater, w:tilePosition, w:allowHighResdownload, w:south, w:restrictionsOnAccess, w:preservicaType, " +
            "   w:north, w:accessConditions, w:internalAccessConditions, w:eadUpdateReviewRequired, w:australianContent, " +
            "   w:moreIlmsDetailsRequired, w:rights, w:genre, w:deliveryUrl, w:recordSource, w:sheetCreationDate, w:creator, " +
            "   w:sheetName, w:coordinates, w:creatorStatement, w:additionalCreator, w:folderType, w:eventNote, " +
            "   w:interactiveIndexAvailable, w:startChild, w:bibLevel, w:holdingNumber, w:publicNotes, w:series, w:constraint1, " +
            "   w:notes, w:catalogueUrl, w:encodingLevel, w:materialFromMultipleSources, w:subject, w:sendToIlms, w:vendorId, " +
            "   w:allowOnsiteAccess, w:language, w:sensitiveMaterial, w:dcmAltPi, w:folderNumber, w:west, w:html, w:preservicaId, " +
            "   w:redocworksReason, w:workCreatedDuringMigration, w:author, w:commentsExternal, w:findingAidNote, w:collection, " +
            "   w:otherTitle, w:imageServerUrl, w:localSystemno, w:acquisitionStatus, w:reorderType, w:immutable, w:copyrightPolicy, " +
            "   w:nextStep, w:publisher, w:additionalSeries, w:tempHolding, w:sortIndex, w:isMissingPage, w:standardId, " +
            "   w:representativeId, w:edition, w:reorder, w:title, w:acquisitionCategory, w:subUnitNo, w:expiryDate, " +
            "   w:digitalStatusDate, w:east, w:contributor, w:publicationCategory, w:ingestJobId, w:subUnitType, w:uniformTitle, " +
            "   w:alias, w:rdsAcknowledgementType, w:issueDate, w:bibId, w:coverage, w:summary, w:additionalContributor, " +
            "   w:sendToIlmsDateTime, w:sensitiveReason, w:carrier, w:form, w:rdsAcknowledgementReceiver, w:digitalStatus, " +
            "   w:dcmRecordCreator, w:sprightlyUrl, w:depositType, w:parentConstraint)")
    public abstract Long insertHistory(@Bind("w") Work work);

    @Override
    @SqlUpdate("update work_history set " +
            "txn_start = :w.txn_start, " +
            "txn_end = :w.txn_end, " +
            "extent = :w.extent, " +
            "dcmDateTimeUpdated = :w.dcmDateTimeUpdated, " +
            "localSystemNumber = :w.localSystemNumber, " +
            "occupation = :w.occupation, " +
            "endDate = :w.endDate, " +
            "displayTitlePage = :w.displayTitlePage, " +
            "holdingId = :w.holdingId, " +
            "hasRepresentation = :w.hasRepresentation, " +
            "totalDuration = :w.totalDuration, " +
            "dcmDateTimeCreated = :w.dcmDateTimeCreated, " +
            "firstPart = :w.firstPart, " +
            "additionalTitle = :w.additionalTitle, " +
            "dcmWorkPid = :w.dcmWorkPid, " +
            "classification = :w.classification, " +
            "commentsInternal = :w.commentsInternal, " +
            "restrictionType = :w.restrictionType, " +
            "ilmsSentDateTime = :w.ilmsSentDateTime, " +
            "subType = :w.subType, " +
            "scaleEtc = :w.subType, " +
            "startDate = :w.startDate, " +
            "dcmRecordUpdater = :w.dcmRecordUpdater, " +
            "tilePosition = :w.tilePosition, " +
            "allowHighResdownload = :w.allowHighResdownload, " +
            "south = :w.south, " +
            "restrictionsOnAccess = :w.restrictionsOnAccess, " +
            "preservicaType = :w.preservicaType, " +
            "north = :w.north, " +
            "accessConditions = :w.accessConditions, " +
            "internalAccessConditions = :w.internalAccessConditions, " +
            "eadUpdateReviewRequired = :w.eadUpdateReviewRequired, " +
            "australianContent = :w.australianContent, " +
            "moreIlmsDetailsRequired = :w.moreIlmsDetailsRequired, " +
            "rights = :w.rights, " +
            "genre = :w.genre, " +
            "deliveryUrl = :w.deliveryUrl, " +
            "recordSource = :w.recordSource, " +
            "sheetCreationDate = :w.sheetCreationDate, " +
            "creator = :w.creator, " +
            "sheetName = :w.sheetName, " +
            "coordinates = :w.coordinates, " +
            "creatorStatement = :w.creatorStatement, " +
            "additionalCreator = :w.additionalCreator, " +
            "folderType = :w.folderType, " +
            "eventNote = :w.eventNote, " +
            "interactiveIndexAvailable = :w.interactiveIndexAvailable, " +
            "startChild = :w.startChild, " +
            "bibLevel = :w.bibLevel, " +
            "holdingNumber = :w.holdingNumber, " +
            "publicNotes = :w.publicNotes, " +
            "series = :w.series, " +
            "constraint1 = :.constraint1, " +
            "notes = :w.notes, " +
            "catalogueUrl = :w.catalogueUrl, " +
            "encodingLevel = :w.encodingLevel, " +
            "materialFromMultipleSources = :w.materialFromMultipleSources, " +
            "subject = :w.subject, " +
            "sendToIlms = :w.sendToIlms, " +
            "vendorId = :w.vendorId, " +
            "allowOnsiteAccess = :w.allowOnsiteAccess, " +
            "language = :w.language, " +
            "sensitiveMaterial = :w.sensitiveMaterial, " +
            "dcmAltPi = :w.dcmAltPi, " +
            "folderNumber = :w.folderNumber, " +
            "west = :w.west, " +
            "html = :w.html, " +
            "preservicaId = :w.preservicaId, " +
            "redocworksReason = :w.redocworksReason, " +
            "workCreatedDuringMigration = :w.workCreatedDuringMigration, " +
            "author = :w.author, " +
            "commentsExternal = :w.commentsExternal, " +
            "findingAidNote = :w.findingAidNote, " +
            "collection = :w.collection, " +
            "otherTitle = :w.otherTitle, " +
            "imageServerUrl = :w.imageServerUrl, " +
            "localSystemno = :w.localSystemno, " +
            "acquisitionStatus = :w.acquisitionStatus, " +
            "reorderType = :w.reorderType, " +
            "immutable = :w.immutable, " +
            "copyrightPolicy = :w.copyrightPolicy, " +
            "nextStep = :w.nextStep, " +
            "publisher = :w.publisher, " +
            "additionalSeries = :w.additionalSeries, " +
            "tempHolding = :w.tempHolding, " +
            "sortIndex = :w.sortIndex, " +
            "isMissingPage = :w.isMissingPage, " +
            "standardId = :w.standardId, " +
            "representativeId = :w.representativeId, " +
            "edition = :w.edition, " +
            "reorder = :w.reorder, " +
            "title = :w.title, " +
            "acquisitionCategory = :w.acquisitionCategory, " +
            "subUnitNo = :w.subUnitNo, " +
            "expiryDate = :w.expiryDate, " +
            "digitalStatusDate = :w.digitalStatusDate, " +
            "east = :w.east, " +
            "contributor = :w.contributor, " +
            "publicationCategory = :w.publicationCategory, " +
            "ingestJobId = :w.ingestJobId, " +
            "subUnitType = :w.subUnitType, " +
            "uniformTitle = :w.uniformTitle, " +
            "alias = :w.alias, " +
            "rdsAcknowledgementType = :w.rdsAcknowledgementType, " +
            "issueDate = :w.issueDate, " +
            "bibId = :w.bibId, " +
            "coverage = :w.coverage, " +
            "summary = :w.summary, " +
            "additionalContributor = :w.additionalContributor, " +
            "sendToIlmsDateTime = :w.sendToIlmsDateTime, " +
            "sensitiveReason = :w.sensitiveReason, " +
            "carrier = :w.carrier, " +
            "form = :w.form, " +
            "rdsAcknowledgementReceiver = :w.rdsAcknowledgementReceiver, " +
            "digitalStatus = :w.digitalStatus, " +
            "dcmRecordCreator = :w.dcmRecordCreator, " +
            "sprightlyUrl = :w.sprightlyUrl, " +
            "depositType = :w.depositType, " +
            "parentConstraint = :w.parentConstraint " +
            "where id = :w.id")
    public abstract Work saveHistory(@BindBean("w") Work work);

    @Override
    @SqlUpdate("delete from work where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);

    @SqlQuery("")
    public abstract Work getParent(@Bind("id") Long id);

    @SqlQuery("")
    public abstract Work getDeliveryWorkParent(@Bind("id") Long id);

    @SqlQuery("")
    public abstract List<Work> getDeliveryWorks(@Bind("id") Long id);
}

