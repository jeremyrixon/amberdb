package amberdb.v2.model.dao;

import amberdb.v2.model.ImageFile;
import amberdb.v2.relation.model.CopyFile;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class ImageFileDao implements CrudDao<ImageFile> {
    @Override
    @SqlQuery("select * from imagefile where id = :id")
    public abstract ImageFile get(@Bind("id") Long id);

    @Override
    @SqlUpdate("INSERT INTO imagefile ( " +
            "extent, fileName, localSystemNumber, software, encodingLevel, language, mimeType, resolution, " +
            "manufacturerSerialNumber, holdingId, resolutionUnit, imageWidth, manufacturerMake, " +
            "manufacturerModelName, encoding, deviceSerialNumber, fileSize, bitDepth, publisher, compression, " +
            "device, imageLength, colourSpace, standardId, title, australianContent, contributor, " +
            "checksum, recordSource, bibId, coverage, orientation, creator, colourProfile, " +
            "checksumGenerationDate, applicationDateCreated, coordinates, creatorStatement, fileFormatVersion, " +
            "dateDigitised, holdingNumber, application, series, blobId, softwareSerialNumber, checksumType, " +
            "location, fileFormat) " +
            "VALUES (" +
            "f:extent, f:fileName, f:localSystemNumber, f:software, f:encodingLevel, f:language, f:mimeType, f:resolution, " +
            "f:manufacturerSerialNumber, f:holdingId, f:resolutionUnit, f:imageWidth, f:manufacturerMake, " +
            "f:manufacturerModelName, f:encoding, f:deviceSerialNumber, f:fileSize, f:bitDepth, f:publisher, f:compression, " +
            "f:device, f:imageLength, f:colourSpace, f:standardId, f:title, f:australianContent, f:contributor, " +
            "f:checksum, f:recordSource, f:bibId, f:coverage, f:orientation, f:creator, f:colourProfile, " +
            "f:checksumGenerationDate, f:applicationDateCreated, f:coordinates, f:creatorStatement, f:fileFormatVersion, " +
            "f:dateDigitised, f:holdingNumber, f:application, f:series, f:blobId, f:softwareSerialNumber, f:checksumType, " +
            "f:location, f:fileFormat);")
    public abstract Long insert(@BindBean("f") ImageFile instance);

    @Override
    @SqlUpdate("UPDATE imagefile SET ( " +
            "extent = f:extent, " +
            "fileName = f:fileName, " +
            "localSystemNumber = f:localSystemNumber, " +
            "software = f:software, " +
            "encodingLevel = f:encodingLevel, " +
            "language = f:language, " +
            "mimeType = f:mimeType, " +
            "resolution = f:resolution, " +
            "manufacturerSerialNumber = f:manufacturerSerialNumber, " +
            "holdingId = f:holdingId, " +
            "resolutionUnit = f:resolutionUnit, " +
            "imageWidth = f:imageWidth, " +
            "manufacturerMake = f:manufacturerMake, " +
            "manufacturerModelName = f:manufacturerModelName, " +
            "encoding = f:encoding, " +
            "deviceSerialNumber = f:deviceSerialNumber, " +
            "fileSize = f:fileSize, " +
            "bitDepth = f:bitDepth, " +
            "publisher = f:publisher, " +
            "compression = f:compression, " +
            "device = f:device, " +
            "imageLength = f:imageLength, " +
            "colourSpace = f:colourSpace, " +
            "standardId = f:standardId, " +
            "title = f:title, " +
            "australianContent = f:australianContent, " +
            "contributor = f:contributor, " +
            "checksum = f:checksum, " +
            "recordSource = f:recordSource, " +
            "bibId = f:bibId, " +
            "coverage = f:coverage, " +
            "orientation = f:orientation, " +
            "creator = f:creator, " +
            "colourProfile = f:colourProfile, " +
            "checksumGenerationDate = f:checksumGenerationDate, " +
            "applicationDateCreated = f:applicationDateCreated, " +
            "coordinates = f:coordinates, " +
            "creatorStatement = f:creatorStatement, " +
            "fileFormatVersion = f:fileFormatVersion, " +
            "dateDigitised = f:dateDigitised, " +
            "holdingNumber = f:holdingNumber, " +
            "application = f:application, " +
            "series = f:series, " +
            "blobId = f:blobId, " +
            "softwareSerialNumber = f:softwareSerialNumber, " +
            "checksumType = f:checksumType, " +
            "location = f:location, " +
            "fileFormat = f:fileFormat" +
            "WHERE id = f:id);")
    public abstract ImageFile save(@BindBean("f") ImageFile instance);

    @Override
    @SqlUpdate("delete from imagefile where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from imagefile_history where id = :id")
    public abstract List<ImageFile> getHistory(@Bind("id") Long id);

    @Override
    @SqlUpdate("INSERT INTO imagefile_history ( " +
            "extent, fileName, localSystemNumber, software, encodingLevel, language, mimeType, resolution, " +
            "manufacturerSerialNumber, holdingId, resolutionUnit, imageWidth, manufacturerMake, " +
            "manufacturerModelName, encoding, deviceSerialNumber, fileSize, bitDepth, publisher, compression, " +
            "device, imageLength, colourSpace, standardId, title, australianContent, contributor, " +
            "checksum, recordSource, bibId, coverage, orientation, creator, colourProfile, " +
            "checksumGenerationDate, applicationDateCreated, coordinates, creatorStatement, fileFormatVersion, " +
            "dateDigitised, holdingNumber, application, series, blobId, softwareSerialNumber, checksumType, " +
            "location, fileFormat) " +
            "VALUES (" +
            "f:extent, f:fileName, f:localSystemNumber, f:software, f:encodingLevel, f:language, f:mimeType, f:resolution, " +
            "f:manufacturerSerialNumber, f:holdingId, f:resolutionUnit, f:imageWidth, f:manufacturerMake, " +
            "f:manufacturerModelName, f:encoding, f:deviceSerialNumber, f:fileSize, f:bitDepth, f:publisher, f:compression, " +
            "f:device, f:imageLength, f:colourSpace, f:standardId, f:title, f:australianContent, f:contributor, " +
            "f:checksum, f:recordSource, f:bibId, f:coverage, f:orientation, f:creator, f:colourProfile, " +
            "f:checksumGenerationDate, f:applicationDateCreated, f:coordinates, f:creatorStatement, f:fileFormatVersion, " +
            "f:dateDigitised, f:holdingNumber, f:application, f:series, f:blobId, f:softwareSerialNumber, f:checksumType, " +
            "f:location, f:fileFormat);")
    public abstract Long insertHistory(@BindBean("f") ImageFile instance);

    @Override
    @SqlUpdate("UPDATE imagefile_history SET ( " +
            "extent = f:extent, " +
            "fileName = f:fileName, " +
            "localSystemNumber = f:localSystemNumber, " +
            "software = f:software, " +
            "encodingLevel = f:encodingLevel, " +
            "language = f:language, " +
            "mimeType = f:mimeType, " +
            "resolution = f:resolution, " +
            "manufacturerSerialNumber = f:manufacturerSerialNumber, " +
            "holdingId = f:holdingId, " +
            "resolutionUnit = f:resolutionUnit, " +
            "imageWidth = f:imageWidth, " +
            "manufacturerMake = f:manufacturerMake, " +
            "manufacturerModelName = f:manufacturerModelName, " +
            "encoding = f:encoding, " +
            "deviceSerialNumber = f:deviceSerialNumber, " +
            "fileSize = f:fileSize, " +
            "bitDepth = f:bitDepth, " +
            "publisher = f:publisher, " +
            "compression = f:compression, " +
            "device = f:device, " +
            "imageLength = f:imageLength, " +
            "colourSpace = f:colourSpace, " +
            "standardId = f:standardId, " +
            "title = f:title, " +
            "australianContent = f:australianContent, " +
            "contributor = f:contributor, " +
            "checksum = f:checksum, " +
            "recordSource = f:recordSource, " +
            "bibId = f:bibId, " +
            "coverage = f:coverage, " +
            "orientation = f:orientation, " +
            "creator = f:creator, " +
            "colourProfile = f:colourProfile, " +
            "checksumGenerationDate = f:checksumGenerationDate, " +
            "applicationDateCreated = f:applicationDateCreated, " +
            "coordinates = f:coordinates, " +
            "creatorStatement = f:creatorStatement, " +
            "fileFormatVersion = f:fileFormatVersion, " +
            "dateDigitised = f:dateDigitised, " +
            "holdingNumber = f:holdingNumber, " +
            "application = f:application, " +
            "series = f:series, " +
            "blobId = f:blobId, " +
            "softwareSerialNumber = f:softwareSerialNumber, " +
            "checksumType = f:checksumType, " +
            "location = f:location, " +
            "fileFormat = f:fileFormat" +
            "WHERE id = f:id);")
    public abstract ImageFile saveHistory(@BindBean("f") ImageFile instance);

    @Override
    @SqlUpdate("delete from imagefile_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);

}
