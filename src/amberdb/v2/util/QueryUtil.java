package amberdb.v2.util;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class QueryUtil {

    private QueryUtil() {}

    public static String quotedCommaSeperatedStrings(List<String> values) {
        return "'" + StringUtils.join(values, "','") + "'";
    }
}
