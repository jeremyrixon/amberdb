package amberdb.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class DateParserTest {
    static final String[] testDateRangeExprs = {
       null,
       "       ",
       " - 1799",
       "1732 - ",
       "1732  ",
       "  1799",
       "c. 1732 - 1799", // circa date
       "1817 - 1916",
       "1817/1916",
       "Mar 1817 - Sep 1916",
       "03.Mar.1817/09.Sep.1916",
       "1732.Mar",
       "1732.Mar.9"
    };
    
    static final String[] dateRangePattern = { 
        "(.*)\\s*-\\s*(.*)",
        "(.*)\\s*/\\s*(.*)"};
    
    static final String[] bulkDateRangePattern = {
        "\\s*\\(bulk (.*)\\s*-\\s*(.*)\\)"
    };
    
    static List<Date> expectedFromDate = new ArrayList<>();
    static List<Date> expectedToDate = new ArrayList<>();
    static final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
    
    @Before
    public void setup() throws ParseException {
        expectedFromDate.add(null);
        expectedFromDate.add(null);
        expectedFromDate.add(null);
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1799"));
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/03/1817"));
        expectedFromDate.add(dateFmt.parse("03/03/1817"));
        expectedFromDate.add(dateFmt.parse("01/03/1732"));
        expectedFromDate.add(dateFmt.parse("09/03/1732"));
        expectedToDate.add(null);
        expectedToDate.add(null);
        expectedToDate.add(dateFmt.parse("31/12/1799"));
        expectedToDate.add(null);
        expectedToDate.add(null);
        expectedToDate.add(null);
        expectedToDate.add(dateFmt.parse("31/12/1799"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("30/09/1916"));
        expectedToDate.add(dateFmt.parse("09/09/1916"));
        expectedToDate.add(null);
    }
    
    @Test 
    public void testBulkDateRangePattern() throws ParseException {
        String testBulkDateRangeExprs = "1894-1935 (bulk 1919-1935)";
        String expectedFromDate = "01/01/1894";
        String expectedToDate = "31/12/1935";
        List<Date> dateRange = DateParser.parseDateRange(testBulkDateRangeExprs);
        assertEquals(expectedFromDate, dateFmt.format(dateRange.get(0)));
        assertEquals(expectedToDate, dateFmt.format(dateRange.get(1)));
    }
    
    @Test
    public void testDateRangePattern() throws ParseException {
        int i = 0;
        for (String dateRangeExpr : testDateRangeExprs) {
            List<Date> dateRange = DateParser.parseDateRange(dateRangeExpr);
            if (dateRangeExpr == null || dateRangeExpr.trim().isEmpty())
                assertNull(dateRange);
            else {
                if (dateRange.isEmpty()) {
                    boolean isFromDate = true;
                    Date fromDate = DateParser.parseDate(dateRangeExpr, isFromDate);
                    assertEquals(dateFmt.format(expectedFromDate.get(i)), dateFmt.format(fromDate));
                } else {
                    if (expectedFromDate.get(i) == null)
                        assertNull(dateRange.get(0));
                    else {
                        if (dateRange.get(0) == null)
                            assertEquals(dateFmt.format(expectedFromDate.get(i)), dateFmt.format(dateRange.get(0)));
                    }

                    if (expectedToDate.get(i) == null)
                        assertNull(dateRange.get(1));
                    else {
                        if (dateRange.get(1) == null)
                            assertEquals(dateFmt.format(expectedToDate.get(i)), dateFmt.format(dateRange.get(1)));
                    }
                }
            }
            i++;
        }
    }
}
