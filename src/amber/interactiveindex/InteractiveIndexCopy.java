package amber.interactiveindex;

import java.util.ArrayList;
import java.util.List;

public class InteractiveIndexCopy {
    private String html;
    private List<InteractiveArea> areas = new ArrayList<>();

    public InteractiveIndexCopy(String html) {
        this.html = html;
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
}

