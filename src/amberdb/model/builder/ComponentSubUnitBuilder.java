package amberdb.model.builder;

import amberdb.enums.BibLevel;
import amberdb.enums.SubUnitType;
import amberdb.model.EADWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentSubUnitBuilder {
    static final Logger log = LoggerFactory.getLogger(ComponentSubUnitBuilder.class);

    public void setSubUnitAndBibLevelFields(EADWork componentWork, String uuid, String componentLevel) {
        if (componentLevel != null && !componentLevel.isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": componentLevel: "
                    + componentLevel.toString());

            SubUnitType subUnitType = SubUnitType.fromString(componentLevel);
            if (subUnitType == null) {
                throw new EADValidationException("INVALID_SUB_UNIT_TYPE", componentLevel, (uuid == null) ? "" : uuid);
            }
            componentWork.setSubUnitType(subUnitType.code());
            // determine bib level with business rule borrowed from DCM
            String bibLevel = mapBibLevel(componentLevel);
            componentWork.setBibLevel(bibLevel);
        }
    }

    private String mapBibLevel(Object componentLevel) {
        String bibLevel = BibLevel.SET.code();
        if (componentLevel != null) {
            if (componentLevel.toString().equalsIgnoreCase("item"))
                bibLevel = BibLevel.ITEM.code();
            else if (componentLevel.toString().equalsIgnoreCase("otherlevel"))
                bibLevel = BibLevel.PART.code();
        }
        return bibLevel;
    }
}
