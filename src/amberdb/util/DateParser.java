package amberdb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParser {
    static final Logger log = LoggerFactory.getLogger(DateParser.class);
    static final String[] dateRangePattern = { 
        "(.*)\\s*-\\s*(.*)",
        "(.*)\\s*/\\s*(.*)",
        "(.*)\\s*or\\s*(.*)",
        "(.*)\\s*,\\s*(.*)"};
    
    static final String[] bulkDateRangePattern = {
        "\\s*\\(bulk (.*)\\s*-\\s*(.*)\\)"
    };
    
    static final String circaDateRangePattern = "c.\\s*(.*)\\s*";
    
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
        "(.*)\\s*[,\\s\\-\\./](\\d{3,4})",
        "(\\d{3,4})"
    };
    
    static final String[] datePatterns = {
        "(\\d{1,2})\\s*[\\s\\-\\./](.*)",
        "(.*)\\s*[,\\s\\-\\./](\\d{1,2})"
    }; 
    
    static final String[] monthAbbr = {
        "JANUARY",
        "FEBRUARY",
        "MARCH",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUGUST",
        "SEPTEMBER",
        "OCTOBER",
        "NOVEMBER",
        "DECEMBER",
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    };  
    static Map<String, Integer> monthLu = new ConcurrentHashMap<>();
    
    static final String DATE_PATTERN1 = "dd/MM/yyyy";
    static final String DATE_PATTERN2 = "dd/MMM/yyyy";
    static Pattern[] dtRangePatterns;
    static Pattern[] yrPatterns;
    static Pattern[] dtPatterns;
    
    static {
        int i = 1;
        for (String month : monthAbbr) {
            int mnth = (i > 12)? i - 12 : i;
            monthLu.put(month, i);
            i++;
        }
        
        dtRangePatterns = new Pattern[dateRangePattern.length*2];
        i = 0;
        for (String expr : dateRangePattern) {
            dtRangePatterns[i] = Pattern.compile(expr + bulkDateRangePattern[0]);
            dtRangePatterns[i+1] = Pattern.compile(expr);
            i=i+2;
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
     * example dates can be parsed:
     *   date range in AS                  start date      end date
     *   null                              null            null
     *   c.1936                            01-01-1936      31-12-1936
     *   c.1930s                           01-01-1930      31-12-1939
     *   1936 or 1937                      01-01-1936      31-12-1937
     *   12 September 1984                 12-09-1984      12-09-1984
     *   [4] June 1937                     04-06-1937      04-06-1937
     *   May 1992                          01-05-1992      31-05-1992
     *   [November] 1935                   01-11-1935      30-11-1935
     *   9-15 December 1938                null            15-12-1938
     *     (Note: current limitation for date parsing as 9 is ambiguous to parse as a start date)
     *   1935-1936                         01-01-1935      31-12-1936
     *   1935-c.1936                       01-01-1935      31-12-1936
     *   1914, 1919-1960 (bulk 1930-1958)  01-01-1914      31-12-1960
     *   
     * @param dateRangeExpr - input date range string
     * @return the from date and to date of the date range in a date list.
     * @throws ParseException
     */
    public static List<Date> parseDateRange(String dateRangeExpr) throws ParseException {
        if (dateRangeExpr == null || dateRangeExpr.trim().isEmpty()) return null;
        
        List<Date> dateRange = new ArrayList<>();      
        dateRangeExpr = dateRangeExpr.replace("[", "").replace("]", "").trim();
        
        // parse circa date
        if (dateRangeExpr.startsWith("c"))
            return parseCircaDateRange(dateRangeExpr);
        
        // parse the date range
        SimpleDateFormat dateFmt1 = new SimpleDateFormat(DATE_PATTERN1);
        List<String> dateRangePair = getExprPair(dateRangeExpr, dtRangePatterns);
        if (dateRangePair == null) {
            Date fromDate = parseDate(dateRangeExpr, true);
            Date toDate = parseDate(dateRangeExpr, false);
            if (fromDate != null) {
                List<Date> sameDayRange = new ArrayList<>();
                sameDayRange.add(fromDate);
                sameDayRange.add(toDate);
                return sameDayRange;
            }
            return null;
        }
        
        if (dateRangePair.size() == 2) {
            if (dateRangePair.get(0).length() == 4) {
                dateRange.add(dateFmt1.parse("01/01/" + dateRangePair.get(0)));
            } else {
                try {
                    List<Date> dates = parseDateRange(dateRangePair.get(0));
                    if (dates != null && dates.size() > 0)
                        dateRange.add(dates.get(0));
                    else
                        dateRange.add(null);
                } catch (ParseException e) {
                    boolean isFromDate = true;
                    dateRange.add(parseDate(dateRangePair.get(0), isFromDate));
                }
            }
            
            if (dateRangePair.get(1).length() == 4) {
                dateRange.add(dateFmt1.parse("31/12/" + dateRangePair.get(1)));
            } else {
                try {
                    List<Date> dates = parseDateRange(dateRangePair.get(1));
                    if (dates != null && dates.size() > 0)
                        dateRange.add(dates.get(dates.size() - 1));
                    else
                        dateRange.add(null);
                } catch (ParseException e) {
                    boolean isFromDate = false;
                    dateRange.add(parseDate(dateRangePair.get(1), isFromDate));
                }
            }
        }
        if (dateRange.isEmpty()) return parseCircaDateRange(dateRangeExpr);
        return dateRange;
    }
    
    /**
     * parseCircaDateRange: returns the from date and to date 10 years apart starting from
     *                      01/01/<circa year>
     */
    protected static List<Date> parseCircaDateRange(String dateRangeExpr) throws ParseException {
        List<Date> dateRange = new ArrayList<>();
        dateRangeExpr = dateRangeExpr.trim();
        boolean addDecade = false;
        if (dateRangeExpr.endsWith("s")) {
            addDecade = true;
            dateRangeExpr = dateRangeExpr.substring(0, dateRangeExpr.length() - 1);
        }
        Pattern circaPattern = Pattern.compile(circaDateRangePattern);
        Matcher matcher = circaPattern.matcher(dateRangeExpr);
        if (matcher.matches()) {
            int startYear = Integer.parseInt(matcher.group(1));
            int endYear = (addDecade)? startYear + 9 : startYear;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
            dateRange.add(dateFormat.parse(startYear + ":01:01"));
            dateRange.add(dateFormat.parse(endYear + ":12:31"));
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
        if (dateExpr == null) return null;
        // parse year
        Map.Entry<Pattern, List<String>> yearAndRestMatch = getMatchedExprPair(dateExpr.trim(), yrPatterns);
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
        if (restExpr == null || restExpr.isEmpty()) 
            return constructDate(isFromDate, year, null, null, mnthAbbrList);
        
        if (mnthAbbrList.contains(restExpr.toUpperCase())) {
            Date potentialDate = constructDate(isFromDate, year, restExpr, null, mnthAbbrList);
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
        if (month == null) {
            month = (isFromDate)?"JAN":"DEC";
        }
       
        SimpleDateFormat dateFmt1 = new SimpleDateFormat(DATE_PATTERN1);
        SimpleDateFormat dateFmt2 = new SimpleDateFormat(DATE_PATTERN2);
        SimpleDateFormat dateFmt = (mnthAbbrList.contains(month.toUpperCase())) ? dateFmt2 : dateFmt1;
        String fmttedMonth = (mnthAbbrList.contains(month.toUpperCase())) ? month.toUpperCase().substring(0,3) : month.toUpperCase();
        
        if (date != null && !date.isEmpty())
            return dateFmt.parse(date + "/" + fmttedMonth + "/" + year);

        if (isFromDate)
            return dateFmt.parse("01/" + fmttedMonth + "/" + year);
        else {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            cal.set(Calendar.MONTH, monthLu.get(month.toUpperCase()) - 1);
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
                if (matcher.find()) {
                    MatchResult result = matcher.toMatchResult();
                    String from = null;
                    String to = null;
                    if (result.groupCount() == 1) {
                        from = matcher.toMatchResult().group(1).trim();
                        to = null;
                    } else if (result.groupCount() > 1) {
                        from = matcher.toMatchResult().group(1).trim();
                        to = matcher.toMatchResult().group(2).trim();
                    }
                    exprPair.add(from);
                    exprPair.add(to);
                    match.put(pattern, exprPair);
                    return match.entrySet().iterator().next();
                }
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
