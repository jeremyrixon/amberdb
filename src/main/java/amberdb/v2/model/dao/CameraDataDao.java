package amberdb.v2.model.dao;

import amberdb.v2.model.CameraData;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class CameraDataDao implements CrudDao<CameraData> {
    @Override
    @SqlQuery("select * from cameradata where id = :id")
    public abstract CameraData get(@Bind("id") Long id);

    @Override
    @SqlUpdate("insert into cameradata (" +
                "id, txn_start, txn_end, extent, exposureTime, localSystemNumber, encodingLevel, whiteBalance, standardId, language, " +
                "lens, title, focalLenth, holdingId, australianContent, contributor, isoSpeedRating, recordSource, coverage, " +
                "bibId, meteringMode, creator, coordinates, fileSource, otherTitle, holdingNumber, exposureProgram, " +
                "exposureFNumber, publisher, scaleEtc, exposureMode, focalLength) " +
            "VALUES (" +
                "c:id, c:txn_start, c:txn_end, c:extent, c:exposureTime, c:localSystemNumber, c:encodingLevel, c:whiteBalance, c:standardId, c:language, " +
                "c:lens, c:title, c:focalLenth, c:holdingId, c:australianContent, c:contributor, c:isoSpeedRating, c:recordSource, c:coverage, " +
                "c:bibId, c:meteringMode, c:creator, c:coordinates, c:fileSource, c:otherTitle, c:holdingNumber, c:exposureProgram, " +
                "c:exposureFNumber, c:publisher, c:scaleEtc, c:exposureMode, c:focalLength); ")
    public abstract Long insert(@BindBean("c") CameraData instance);

    @Override
    @SqlUpdate("update cameradata set " +
            "id = c:id, " +
            "txn_start = c:txn_start, " +
            "txn_end = c:txn_end, " +
            "extent = c:extent, " +
            "exposureTime = c:exposureTime, " +
            "localSystemNumber = c:localSystemNumber, " +
            "encodingLevel = c:encodingLevel, " +
            "whiteBalance = c:whiteBalance, " +
            "standardId = c:standardId, "+
            "language = c:language, " +
            "lens = c:lens, " +
            "title = c:title, " +
            "focalLenth = c:focalLength, " +
            "holdingId = c:holdingId, " +
            "australianContent = c:australianContent, " +
            "contributor = c:contributor, " +
            "isoSpeedRating = c:isoSpeedRating, " +
            "recordSource = c:recordSource, " +
            "coverage = c:coverage, " +
            "bibId = c:bibId, " +
            "meteringMode = c:meteringMode, " +
            "creator = c:creator, " +
            "coordinates = c:coordinates, " +
            "fileSource = c:fileSource, " +
            "otherTitle = c:otherTitle, " +
            "holdingNumber = c:holdingNumber, " +
            "exposureProgram = c:exposureProgram, " +
            "exposureFNumber = c:exposureFNumber, " +
            "publisher = c:publisher, " +
            "scaleEtc = c:scaleEtc, " +
            "exposureMode = c:exposureMode, " +
            "focalLength = c:focalLength " +
            "where id = c:id")
    public abstract CameraData save(@BindBean("c") CameraData instance);

    @Override
    @SqlUpdate("delete from cameradata where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from cameradata_history where id = :id")
    public abstract List<CameraData> getHistory(@Bind("id") Long id);

    @Override
    @SqlUpdate("insert into cameradata_history (" +
            "id, txn_start, txn_end, extent, exposureTime, localSystemNumber, encodingLevel, whiteBalance, standardId, language, " +
            "lens, title, focalLenth, holdingId, australianContent, contributor, isoSpeedRating, recordSource, coverage, " +
            "bibId, meteringMode, creator, coordinates, fileSource, otherTitle, holdingNumber, exposureProgram, " +
            "exposureFNumber, publisher, scaleEtc, exposureMode, focalLength) " +
            "VALUES (" +
            "c:id, c:txn_start, c:txn_end, c:extent, c:exposureTime, c:localSystemNumber, c:encodingLevel, c:whiteBalance, c:standardId, c:language, " +
            "c:lens, c:title, c:focalLenth, c:holdingId, c:australianContent, c:contributor, c:isoSpeedRating, c:recordSource, c:coverage, " +
            "c:bibId, c:meteringMode, c:creator, c:coordinates, c:fileSource, c:otherTitle, c:holdingNumber, c:exposureProgram, " +
            "c:exposureFNumber, c:publisher, c:scaleEtc, c:exposureMode, c:focalLength); ")
    public abstract Long insertHistory(@BindBean("c") CameraData instance);

    @Override
    @SqlUpdate("update cameradata_history set " +
            "id = c:id, " +
            "txn_start = c:txn_start, " +
            "txn_end = c:txn_end, " +
            "extent = c:extent, " +
            "exposureTime = c:exposureTime, " +
            "localSystemNumber = c:localSystemNumber, " +
            "encodingLevel = c:encodingLevel, " +
            "whiteBalance = c:whiteBalance, " +
            "standardId = c:standardId, "+
            "language = c:language, " +
            "lens = c:lens, " +
            "title = c:title, " +
            "focalLenth = c:focalLength, " +
            "holdingId = c:holdingId, " +
            "australianContent = c:australianContent, " +
            "contributor = c:contributor, " +
            "isoSpeedRating = c:isoSpeedRating, " +
            "recordSource = c:recordSource, " +
            "coverage = c:coverage, " +
            "bibId = c:bibId, " +
            "meteringMode = c:meteringMode, " +
            "creator = c:creator, " +
            "coordinates = c:coordinates, " +
            "fileSource = c:fileSource, " +
            "otherTitle = c:otherTitle, " +
            "holdingNumber = c:holdingNumber, " +
            "exposureProgram = c:exposureProgram, " +
            "exposureFNumber = c:exposureFNumber, " +
            "publisher = c:publisher, " +
            "scaleEtc = c:scaleEtc, " +
            "exposureMode = c:exposureMode, " +
            "focalLength = c:focalLength " +
            "where id = c:id")
    public abstract CameraData saveHistory(@BindBean("c") CameraData instance);

    @Override
    @SqlUpdate("delete from cameradata_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);
}
