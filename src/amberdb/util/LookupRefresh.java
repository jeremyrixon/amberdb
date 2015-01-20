package amberdb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import amberdb.AmberSession;
import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;

public class LookupRefresh {

    public void synchronizeLookups(AmberSession db) {
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:cache", "refresh", "lookups");
        DBI lookupsDbi = new DBI(ds);
        LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
        if (!luSchema.schemaTablesExist()) {
            luSchema.createLookupsSchema();
        }

        Map<String, ListLu> fromMap = indexLookups(lookupsDbi.onDemand(Lookups.class).findActiveLookups());
        Map<String, ListLu> toMap = indexLookups(db.getLookups().findActiveLookups());
        for (String nameCode : fromMap.keySet()) {
            if (toMap.get(nameCode) == null) {               
                String name = nameCode.substring(0, nameCode.indexOf('_') - 1);
                String code = nameCode.substring(nameCode.indexOf('_') + 1);
                String value = fromMap.get(nameCode).getValue();
                System.out.println("adding lookups for name: " + name + ", code: " + code + ", value: " + value);
                
                // add new lookup entry
                db.getLookups().addLookup(fromMap.get(nameCode));
            } else {
                // update an existing lookup entry
                System.out.println("updating lookups for " + nameCode);
                Long id = toMap.get(nameCode).getId();
                db.getLookups().updateLookup(id, fromMap.get(nameCode).getValue());
            }
        }
        db.commit();
    }

    Map<String, ListLu> indexLookups(List<ListLu> lookups) {
        Map<String, ListLu> index = new HashMap<>();
        if (lookups == null) return index;
        for (ListLu entry : lookups) {
            index.put(entry.getName() + "_" + entry.getCode(), entry);
        }
        return index;
    }
}
