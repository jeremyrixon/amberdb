package amber.interactiveindex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class InteractiveIndexCopy {
    private String html;
    private List<SiblingEditions> siblingEditionsList = new ArrayList<>();
    private List<InteractiveArea> areas = new ArrayList<>();

    public InteractiveIndexCopy(String html) {
        this.html = html;
    }

    public InteractiveIndexCopy(){

    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<InteractiveArea> getAreas() {
        return areas;
    }

    public void setAreas(List<InteractiveArea> areas) {
        this.areas = areas;
    }

    public void addInteractiveArea(InteractiveArea interactiveArea) {
        areas.add(interactiveArea);
    }

    public void clearAreas(){
        areas.clear();
    }

    public List<SiblingEditions> getSiblingEditionsList() {
        return siblingEditionsList;
    }

    public void setSiblingEditionsList(List<SiblingEditions> siblingEditionsList) {
        this.siblingEditionsList = siblingEditionsList;
    }

    public InteractiveArea getInteractiveArea(String objectId){
        for (InteractiveArea interactiveArea : areas){
            if (StringUtils.equalsIgnoreCase(interactiveArea.getObjectId(), objectId)){
                return interactiveArea;
            }
        }
        return null;
    }

    public void addSiblingEdition(List<String> objIdList) {
        if (CollectionUtils.isNotEmpty(objIdList) && objIdList.size() > 1){
            siblingEditionsList.add(new SiblingEditions(objIdList));    
        }
    }
    
    public List<String> getSiblingEditionObjectIds(String objId){
        for (SiblingEditions siblingEditions : siblingEditionsList){
            if (siblingEditions.contains(objId)){
                return siblingEditions.getAllSiblingEditionObjectIdsBut(objId);
            }
        }
        return null;
    }
}

