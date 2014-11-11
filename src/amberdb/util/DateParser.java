package amberdb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParser {
    static final Logger log = LoggerFactory.getLogger(DateParser.class);
    static final String[] dateRangePattern = { 
        "(.*)\\s*-\\s*(.*)",
        "(.*)\\s*/\\s*(.*)"};
    
    static final String[] yearPatterns = {
        "(\\d{3,4})\\s*[\\s\\-\\./](.*)", // which covers
                                          // "(\\d\\d\\d)\\s+(.*)",
                                          // "(\\d\\d\\d)\\s*-\\s*(.*)",
                                          // "(\\d\\d\\d)\\s*/\\s*(.*)",
                                          // "(\\d\\d\\d)\\s*.\\s*(.*)",
                                          // "(\\d\\d\\d\\d)\\s+(.*)",
                                          // "(\\d\\d\\d\\d)\\s*-\\s*(.*)",
                                          // "(\\d\\d\\d\\d)\\s*/\\s*(.*)",
                                          // "(\\d\\d\\d\\d)\\s*.\\s*(.*)",
        "(.*)\\s*[,\\s\\-\\./](\\d{3,4})"
    };
    
    static final String[] datePatterns = {
        "(\\d{1,2})\\s*[\\s\\-\\./](.*)",
        "(.*)\\s*[,\\s\\-\\./](\\d{1,2})"
    }; 
    
    static String[] monthAbbr = new String[24];  
    static Map<String, Month> monthLu = new ConcurrentHashMap<>();
    
    static final Calendar cal = Calendar.getInstance();
    static final SimpleDateFormat dateFmt1 = new SimpleDateFormat("dd/MM/yyyy");
    static final SimpleDateFormat dateFmt2 = new SimpleDateFormat("dd/MMM/yyyy");
    static Pattern[] dtRangePatterns;
    static Pattern[] yrPatterns;
    static Pattern[] dtPatterns;
    
    static {
        int i = 0;
        for (Month month : Month.values()) {
            monthAbbr[i] = month.toString();
            monthAbbr[i+1] = monthAbbr[i].substring(0,3);
            monthLu.put(monthAbbr[i], month);
            monthLu.put(monthAbbr[i+1], month);
            i++;
            i++;
        }
        
        dtRangePatterns = new Pattern[dateRangePattern.length];
        i = 0;
        for (String expr : dateRangePattern) {
            dtRangePatterns[i] = Pattern.compile(expr);
            i++;
        }
        yrPatterns = new Pattern[yearPatterns.length];
        i = 0;
        for (String yearPattern : yearPatterns) {
            yrPatterns[i] = Pattern.compile(yearPattern);
            i++;
        }
        dtPatterns = new Pattern[datePatterns.length];
        i = 0;
        for (String dtPattern : datePatterns) {
            dtPatterns[i] = Pattern.compile(dtPattern);
            i++;
        }
    }
    
    /**
     * parseDateRange: returns the from date and to date in a list of date.
     * @param dateRangeExpr - input date range string
     * @return the from date and to date of the date range in a date list.
     * @throws ParseException
     */
    public static List<Date> parseDateRange(String dateRangeExpr) throws ParseException {
        List<Date> dateRange = new ArrayList<>();
        
        // trim circa date
        dateRangeExpr = dateRangeExpr.trim();
        if (dateRangeExpr.startsWith("c."))
            dateRangeExpr = dateRangeExpr.replace("c.", "").trim();
        else if (dateRangeExpr.startsWith("c"))
            dateRangeExpr = dateRangeExpr.replace("c", "").trim();
        
        // parse the date range
        List<String> dateRangePair = getExprPair(dateRangeExpr, dtRangePatterns);
        if (dateRangePair != null && dateRangePair.size() == 2) {
            if (dateRangePair.get(0).length() == 4) {
                dateRange.add(dateFmt1.parse("01/01/" + dateRangePair.get(0)));
            } else {
                boolean isFromDate = true;
                dateRange.add(parseDate(dateRangePair.get(0), isFromDate));
            }
            
            if (dateRangePair.get(1).length() == 4) {
                dateRange.add(dateFmt1.parse("31/12/" + dateRangePair.get(1)));
            } else {
                boolean isFromDate = false;
                dateRange.add(parseDate(dateRangePair.get(1), isFromDate));
            }
        }
        return dateRange;
    }
    
    /**
     * parseDate: returns a date approximated from the date expression.
     * @param dateExpr - input date string
     * @param isFromDate - flag indicating whether this is a from date or to date
     * @return the parsed date
     * @throws ParseException
     */
    public static Date parseDate(String dateExpr, boolean isFromDate) throws ParseException {
        // parse year
        Map.Entry<Pattern, List<String>> yearAndRestMatch = getMatchedExprPair(dateExpr, yrPatterns);
        if (yearAndRestMatch == null) return null;
        
        String year = "";
        String restExpr = "";
        String yrPattern = yearAndRestMatch.getKey().pattern();
        if (yrPattern != null && (yrPattern.startsWith("(\\d") || yrPattern.startsWith("c (\\d"))) {
            year = yearAndRestMatch.getValue().get(0);
            restExpr = yearAndRestMatch.getValue().get(1);
        } else {
            restExpr = yearAndRestMatch.getValue().get(0);
            year = yearAndRestMatch.getValue().get(1);
        }
        
        // parse month
        List<String> mnthAbbrList = Arrays.asList(monthAbbr);
        if (mnthAbbrList.contains(restExpr.toUpperCase())) {
            Date potentialDate = constructDate(isFromDate, year, restExpr, null, mnthAbbrList);
            String ddStr = dateFmt2.format(potentialDate);
            if (potentialDate != null) return potentialDate;
        }
        
        // parse date
        Map.Entry<Pattern, List<String>> dateAndRestMatch = getMatchedExprPair(restExpr, dtPatterns);
        if (dateAndRestMatch == null) return null;
        String date = "";
        String month = "";
        String dtPattern = dateAndRestMatch.getKey().pattern();
        if (dtPattern != null && (dtPattern.startsWith("(\\d"))) {
            date = dateAndRestMatch.getValue().get(0);
            month = dateAndRestMatch.getValue().get(1);
        } else {
            month = dateAndRestMatch.getValue().get(0);
            date = dateAndRestMatch.getValue().get(1);            
        }
        return constructDate(isFromDate, year, month, date, mnthAbbrList);
    }

    /**
     * constructDate: construct a date base on available year, month and date. If date is not provided,
     *                it will be approximately base on whether it's a from date or to date.
     * @param isFromDate - flag indicate whether this is a from date or a to date
     * @param year       - year to construct date from
     * @param month      - month to construct date from
     * @param date       - date to construct date from
     * @param mnthAbbrList - list of abbrivatted month
     * @return the constructed date
     * @throws ParseException
     */
    private static Date constructDate(boolean isFromDate, String year, String month, String date,
            List<String> mnthAbbrList) throws ParseException {
        SimpleDateFormat dateFmt = (mnthAbbrList.contains(month.toUpperCase())) ? dateFmt2 : dateFmt1;
        String fmttedMonth = (mnthAbbrList.contains(month.toUpperCase())) ? month.toUpperCase().substring(0,3) : month.toUpperCase();
        
        if (date != null && !date.isEmpty())
            return dateFmt.parse(date + "/" + fmttedMonth + "/" + year);

        if (isFromDate)
            return dateFmt.parse("01/" + fmttedMonth + "/" + year);
        else {
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            cal.set(Calendar.MONTH, monthLu.get(month.toUpperCase()).getValue());
            // Note: not sure sometimes time is over clocked, hence the work around below
            Date newTime = dateFmt.parse("" + cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + fmttedMonth + "/" + year);
            SimpleDateFormat fmt = new SimpleDateFormat("MMM");
            String monthInNewTime = fmt.format(newTime);
            if (!monthInNewTime.toUpperCase().equals(fmttedMonth))
                return new Date(newTime.getTime() - 2);            
            return newTime;
        }
    }
    
    /**
     * getExprPair: returns the pair of matched expressions passed from the input expr.
     * @param expr - input string
     * @param patterns - patterns containing 2 groups of expressions to be matched
     * @return the pair of matched expressions passed from the input expr.
     */
    private static List<String> getExprPair(String expr, Pattern...patterns) {
        Map.Entry<Pattern, List<String>> match = getMatchedExprPair(expr, patterns);
        if (match == null) return null;
        return match.getValue();
    }
    
    /**
     * getMatchedExprPair: returns a map of matched pattern, and the matched result strings in a list
     * @param expr - input string
     * @param patterns - patterns containing 2 groups of expressions to be matched
     * @return a map of matched pattern, and the matched result strings in a list
     */
    private static Map.Entry<Pattern, List<String>> getMatchedExprPair(String expr, Pattern...patterns) {
        Map<Pattern, List<String>> match = new ConcurrentHashMap<>();
        List<String> exprPair = new ArrayList<>();
        IllegalStateException error = null;
        for (Pattern pattern : patterns) {
            try {
                Matcher matcher = pattern.matcher(expr);
                matcher.find();
                String from = matcher.group(1).trim();
                String to = matcher.group(2).trim();
                exprPair.add(from);
                exprPair.add(to);
                match.put(pattern, exprPair);
                return match.entrySet().iterator().next();
            } catch (IllegalStateException e) {
                // if landed this exception, then try to parse the expression with the next pattern
                error = e;
            }
        }
        
        // ok, this expression does not match any pattern defined, need to report this expression in order
        // to have its pattern defined.
        if (error != null) 
            throw new RuntimeException("Failed to parse expr : " + expr + " as "+ error.getMessage(), error);
        return null;
    }
}
