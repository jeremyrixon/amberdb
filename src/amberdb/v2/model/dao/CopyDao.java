package amberdb.v2.model.dao;

import amberdb.v2.model.Copy;
import amberdb.v2.relation.model.WorkCopy;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class CopyDao implements CrudDao<Copy> {

    @Override
    @SqlQuery("select * from copy where id = :id")
    public abstract Copy get(@Bind("id") Long id);

    @Override
    @SqlUpdate("INSERT INTO copy (" +
            "id, txn_start, txn_end, dcmDateTimeUpdated, extent, dcmRecordUpdater, localSystemNumber, encodingLevel, " +
            "standardId, language, title, holdingId, internalAccessConditions, australianContent, dateCreated, " +
            "contributor, timedStatus, copyType, alias, copyStatus, copyRole, manipulation, recordSource, algorithm " +
            "bibId, creator, otherNumbers, dcmDateTimeCreated, materialType, commentsExternal, coordinates " +
            "creatorStatement, classification, currentVersion, commentsInternal, bestCopy, carrier, holdingNumber " +
            "series, publisher, dcmRecordCreator, dcmCopyPid) " +
            "VALUES (" +
            "c:id, c:txn_start, c:txn_end, c:dcmDateTimeUpdated, c:extent, c:dcmRecordUpdater, c:localSystemNumber, c:encodingLevel, " +
            "c:standardId, c:language, c:title, c:holdingId, c:internalAccessConditions, c:australianContent, c:dateCreated, " +
            "c:contributor, c:timedStatus, c:copyType, c:alias, c:copyStatus, c:copyRole, c:manipulation, c:recordSource, c:algorithm " +
            "c:bibId, c:creator, c:otherNumbers, c:dcmDateTimeCreated, c:materialType, c:commentsExternal, c:coordinates " +
            "c:creatorStatement, c:classification, c:currentVersion, c:commentsInternal, c:bestCopy, c:carrier, c:holdingNumber " +
            "c:series, c:publisher, c:dcmRecordCreator, c:dcmCopyPid)")
    public abstract Long insert(@BindBean("c") Copy instance);

    @Override
    @SqlUpdate("UPDATE copy SET " +
            "id = c:id, " +
            "txn_start = c:txn_start, " +
            "txn_end = c:txn_end, " +
            "dcmDateTimeUpdated = c:dcmDateTimeUpdated, " +
            "extent = c:extent, " +
            "dcmRecordUpdater = c:dcmRecordUpdater, " +
            "localSystemNumber = c:localSystemNumber, " +
            "encodingLevel = c:encodingLevel, " +
            "standardId = c:standardId, " +
            "language = c:language, " +
            "title = c:title, " +
            "holdingId = c:holdingId, " +
            "internalAccessConditions = c:internalAccessConditions, " +
            "australianContent = c:australianContent, " +
            "dateCreated = c:dateCreated, " +
            "contributor = c:contributor, " +
            "timedStatus = c:timedStatus, " +
            "copyType = c:copyType, " +
            "alias = c:alias, " +
            "copyStatus = c:copyStatus, " +
            "copyRole = c:copyRole, " +
            "manipulation = c:manipulation, " +
            "recordSource = c:recordSource, " +
            "algorithm = c:algorithm, " +
            "bibId = c:bibId, " +
            "creator = c:creator, " +
            "otherNumbers = c:otherNumbers, " +
            "dcmDateTimeCreated = c:dcmDateTimeCreated, " +
            "materialType = c:materialType, " +
            "commentsExternal = c:commentsExternal, " +
            "coordinates = c:coordinates, " +
            "creatorStatement = c:creatorStatement, " +
            "classification = c:classification, " +
            "currentVersion = c:currentVersion, " +
            "commentsInternal = c:commentsInternal, " +
            "bestCopy = c:bestCopy, " +
            "carrier = c:carrier, " +
            "holdingNumber = c:holdingNumber, " +
            "series = c:series, " +
            "publisher = c:publisher, " +
            "dcmRecordCreator = c:dcmRecordCreator, " +
            "dcmCopyPid = c:dcmCopyPid " +
            "WHERE id = c:id")
    public abstract Copy save(@BindBean("c") Copy instance);

    @Override
    @SqlUpdate("delete from copy where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from copy_history where id = :id")
    public abstract List<Copy> getHistory(@Bind("id") Long id);

    @Override
    @SqlUpdate("insert into copy_history (" +
            "id, txn_start, txn_end, dcmDateTimeUpdated, extent, dcmRecordUpdater, localSystemNumber, encodingLevel, " +
            "standardId, language, title, holdingId, internalAccessConditions, australianContent, dateCreated, " +
            "contributor, timedStatus, copyType, alias, copyStatus, copyRole, manipulation, recordSource, algorithm " +
            "bibId, creator, otherNumbers, dcmDateTimeCreated, materialType, commentsExternal, coordinates " +
            "creatorStatement, classification, currentVersion, commentsInternal, bestCopy, carrier, holdingNumber " +
            "series, publisher, dcmRecordCreator, dcmCopyPid) " +
            "VALUES (" +
            "c:id, c:txn_start, c:txn_end, c:dcmDateTimeUpdated, c:extent, c:dcmRecordUpdater, c:localSystemNumber, c:encodingLevel, " +
            "c:standardId, c:language, c:title, c:holdingId, c:internalAccessConditions, c:australianContent, c:dateCreated, " +
            "c:contributor, c:timedStatus, c:copyType, c:alias, c:copyStatus, c:copyRole, c:manipulation, c:recordSource, c:algorithm " +
            "c:bibId, c:creator, c:otherNumbers, c:dcmDateTimeCreated, c:materialType, c:commentsExternal, c:coordinates " +
            "c:creatorStatement, c:classification, c:currentVersion, c:commentsInternal, c:bestCopy, c:carrier, c:holdingNumber " +
            "c:series, c:publisher, c:dcmRecordCreator, c:dcmCopyPid)")
    public abstract Long insertHistory(@BindBean("c") Copy instance);

    @Override
    @SqlUpdate("update copy_history set " +
            "id = c:id, " +
            "txn_start = c:txn_start, " +
            "txn_end = c:txn_end, " +
            "dcmDateTimeUpdated = c:dcmDateTimeUpdated, " +
            "extent = c:extent, " +
            "dcmRecordUpdater = c:dcmRecordUpdater, " +
            "localSystemNumber = c:localSystemNumber, " +
            "encodingLevel = c:encodingLevel, " +
            "standardId = c:standardId, " +
            "language = c:language, " +
            "title = c:title, " +
            "holdingId = c:holdingId, " +
            "internalAccessConditions = c:internalAccessConditions, " +
            "australianContent = c:australianContent, " +
            "dateCreated = c:dateCreated, " +
            "contributor = c:contributor, " +
            "timedStatus = c:timedStatus, " +
            "copyType = c:copyType, " +
            "alias = c:alias, " +
            "copyStatus = c:copyStatus, " +
            "copyRole = c:copyRole, " +
            "manipulation = c:manipulation, " +
            "recordSource = c:recordSource, " +
            "algorithm = c:algorithm, " +
            "bibId = c:bibId, " +
            "creator = c:creator, " +
            "otherNumbers = c:otherNumbers, " +
            "dcmDateTimeCreated = c:dcmDateTimeCreated, " +
            "materialType = c:materialType, " +
            "commentsExternal = c:commentsExternal, " +
            "coordinates = c:coordinates, " +
            "creatorStatement = c:creatorStatement, " +
            "classification = c:classification, " +
            "currentVersion = c:currentVersion, " +
            "commentsInternal = c:commentsInternal, " +
            "bestCopy = c:bestCopy, " +
            "carrier = c:carrier, " +
            "holdingNumber = c:holdingNumber, " +
            "series = c:series, " +
            "publisher = c:publisher, " +
            "dcmRecordCreator = c:dcmRecordCreator, " +
            "dcmCopyPid = c:dcmCopyPid " +
            "where id = c:id")
    public abstract Copy saveHistory(@BindBean("c") Copy instance);

    @Override
    @SqlUpdate("delete from copy_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);

    @SqlQuery("select * from work_copy where work_id = :id")
    public abstract List<WorkCopy> getCopiesByWorkId(@Bind("id") Long id);

    @SqlQuery("select * from work_copy where work_id = :id and copy_role = :role")
    public abstract WorkCopy getCopyByWorkIdAndRole(@Bind("id") Long id, @Bind("role") String copyRole);

    @SqlQuery("select * from work_copy where copy_id = :id;")
    public abstract WorkCopy getWorkByCopyId(@Bind("id") Long id);

    @SqlQuery("select * from work_copy where work_id = :id order by copy_id;")
    public abstract List<WorkCopy> getOrderedCopyIds(@Bind("id") Long id);
}
