package amberdb.query;

public class WorkProperty {
    private final String name;
    private final String value;

    public WorkProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
