package amberdb.v2.model.dao;

import amberdb.v2.model.Acknowledgement;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class AcknowledgementDao implements CrudDao<Acknowledgement> {
    @Override
    @SqlQuery("select * from acknowledgement where id = :id")
    public abstract Acknowledgement get(@Bind("id") Long id);

    @Override
    public abstract Long insert(Acknowledgement instance);

    @Override
    public abstract Acknowledgement save(Acknowledgement instance);

    @Override
    @SqlQuery("delete from acknowledgement where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from acknowledgement_history where id = :id")
    public abstract List<Acknowledgement> getHistory(Long id);

    @Override
    public abstract Long insertHistory(Acknowledgement instance);

    @Override
    public abstract Acknowledgement saveHistory(Acknowledgement instance);

    @Override
    @SqlQuery("delete from acknowledgement_history where id = :id")
    public abstract void deleteHistory(Long id);
}
