package amberdb.structmap;

import java.util.ArrayList;
import java.util.List;
import amberdb.MapStrategy;
import amberdb.model.Copy;
import amberdb.model.Node;
import amberdb.model.Work;

public class WorkFileMapStrategy extends MapStrategy {

    /**
     * Given a parent work as the context (or group holder), create and return a list of sub-work items
     * mapped into the parent work structmap base on supplied files through their fileLocations.
     * 
     * @param work: the group holder for a list of sub items.
     * @param fileLocations: a list of file paths for the files to be ingested.
     * @return a list of sub-work items for these input files.
     */
    public <Work extends Node, Copy extends Node> List<Work> map(Work work, List<Copy> copies) {
        if (copies == null)
            throw new IllegalArgumentException("Cannot map to work as input copies is null.");
        
        List<Work> items = new ArrayList<Work>();
        for (Copy copy : copies) {
            amberdb.model.Work item = dao.addWork();
            item.addCopy((amberdb.model.Copy) copy);
            ((amberdb.model.Work) work).addChild(item);
            items.add((Work) item);
        }
        return items;
    }
}
