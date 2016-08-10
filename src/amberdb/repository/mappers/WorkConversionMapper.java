package amberdb.repository.mappers;

import amberdb.repository.model.Section;
import amberdb.repository.model.Work;

public enum WorkConversionMapper {
    INSTANCE;

    private WorkConversionMapper() {
    }

    public Section asSection(Work w) {
        Section s = new Section();
        s.setId(w.getId());
        s.setTxnStart(w.getTxnStart());
        s.setTxnEnd(w.getTxnEnd());
        s.setType(w.getType());

        return s;
    }
}
