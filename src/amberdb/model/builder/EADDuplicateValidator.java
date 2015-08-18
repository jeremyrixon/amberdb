package amberdb.model.builder;

import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EADDuplicateValidator {

    public EADDuplicateValidator(XmlDocumentParser parser) {
        this.parser = parser;
    }

    private XmlDocumentParser parser;

    public void validate() {
        Set<String> dupUuids = getDuplicateUUIDs();
        if (dupUuids.size() != 0) {
            if (dupUuids.contains("")) {
                throw new EADValidationException("MISSING_UUID_DETECTED", "");
            }
            throw new EADValidationException("DUPLICATE_UUID", Joiner.on(", ").join(dupUuids));
        }
    }

    private Set<String> getDuplicateUUIDs() {
        List<String> eadUUIDList = parser.getUUIDsAsList(100);
        Set<String> dupDetector = new HashSet<>();
        Iterator<String> iter = eadUUIDList.iterator();
        while(iter.hasNext()) {
            String curr = iter.next();
            if (!dupDetector.contains(curr)) {
                iter.remove();
            }
            dupDetector.add(curr);
        }
        return new HashSet<>(eadUUIDList);
    }
}
