package amberdb.repository;

import java.io.IOException;
import java.util.Properties;

public enum DbProperties {
    DB_TYPE("db.type"),
    DB_URL("amber.url"),
    DB_USERNAME("amber.user"),
    DB_PASSWORD("amber.pass");

    private final static Properties dbProperties = new Properties();
    private static String propsFilename = "db.properties";

    private final String key;

    static {
        try {
            dbProperties.load(DbProperties.class.getClassLoader().getResourceAsStream(propsFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DbProperties(String key) {
        this.key = key;
    }

    public String val() {
        return dbProperties.getProperty(key, "");
    }
}
