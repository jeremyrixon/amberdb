package amberdb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParser {
    static final Logger log = LoggerFactory.getLogger(DateParser.class);
    static String[] datePatterns = { 
    "dd/MM/yyyy",
    "dd MMM yyyy",
    "MMM dd, yyyy",
    "EEE, MMM d, yyyy",
    "EEE, d MMM yyyy",
    "yyy MM dd",
    "yyyy.MM.dd",
    "yyyy-MM-dd" };
    
    static final String[] dateRangePattern = { 
        "(\\d\\d\\d\\d)\\s*-\\s*(\\d\\d\\d\\d)",
        "(\\d\\d\\d\\d)\\s*/\\s*(\\d\\d\\d\\d)",
        "(\\w*)\\s*-\\s*(\\w*)",
        "(\\w*)\\s*/\\s*(\\w*)"};
    
    static List<SimpleDateFormat> dateFormats = new ArrayList<>();
    {
        for (String datePattern : datePatterns) {
            dateFormats.add(new SimpleDateFormat(datePattern));
        }
    }
    public static Date parse(String dateStr) {
        for (int i = 0; i < dateFormats.size(); i++) {
            try {
                return dateFormats.get(i).parse(dateStr);
            } catch (ParseException e) { 
                // ok, the input date string is not in this pattern format
            }
        }
        throw new IllegalArgumentException("The input date " + dateStr + " does not match any of the known date patterns.");
    }
    
    public static List<Date> parseDateRange(Object dateRange) {
        try {
            // parsing date range
            String dateRangeStr = dateRange.toString();
            List<Date> dateList = new ArrayList<>();
            if (StringUtils.countMatches(dateRangeStr, "-") == 1) {
                String[] dateStrs = StringUtils.split(dateRangeStr, '-');
                for (String dateStr : dateStrs) {
                    if (dateStr != null && !dateStr.trim().isEmpty()) {
                        dateList.add(DateParser.parse(dateStr.trim()));
                    }
                }
            } else {
                dateList.add(DateParser.parse(dateRangeStr.trim()));
            }
            return dateList;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
