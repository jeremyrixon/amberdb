package amberdb.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DateParserTest {
    static final String[] testDateRangeExprs = {
        "c. 1732 - 1799", // circa date
        "1817 - 1916",
        "1817/1916",
        "Mar 1817 - Sep 1916",
        "03.Mar.1817/09.Sep.1916"
    };
    
    static List<Date> expectedFromDate = new ArrayList<>();
    static List<Date> expectedToDate = new ArrayList<>();
    static final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
    
    @Before
    public void setup() throws ParseException {
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/03/1817"));
        expectedFromDate.add(dateFmt.parse("03/03/1817"));
        expectedToDate.add(dateFmt.parse("31/12/1799"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("30/09/1916"));
        expectedToDate.add(dateFmt.parse("09/09/1916"));
    }
    
    @Test
    public void testDateRangePattern() throws ParseException {     
        int i = 0;
        for (String dateRangeExpr : testDateRangeExprs) {
            List<Date> dateRange = DateParser.parseDateRange(dateRangeExpr);
            assertEquals(expectedFromDate.get(i), dateRange.get(0));
            assertEquals(expectedToDate.get(i), dateRange.get(1));
            i++;
        }
    }
}
