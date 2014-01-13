package amberdb.sql;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class AmberPropertyTest {
    private static String str;
    private static Integer number;
    private static Long lnumber;
    private static Boolean bool;
    private static Float fnumber;
    private static Double dnumber;
    private static Date date;
    private static List<String> list;
    
    @BeforeClass
    public static void setup() {
        str = "string";
        number = 123;
        lnumber = 123456L;
        bool = true;
        fnumber = (float) 123.45;
        dnumber = (double) 12354;
        
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyyMMdd'T'HHmmss");
        try {
            date = fmt.parse("19710101T151515");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        list = new ArrayList<String>();
    }
    
    @Test
    public void testEncodeDecodeAmberDataTypes() {
        byte[] strVal = AmberProperty.encodeBlob(str);
        byte[] numberVal = AmberProperty.encodeBlob(number);
        byte[] lnumberVal = AmberProperty.encodeBlob(lnumber);
        byte[] boolVal = AmberProperty.encodeBlob(bool);
        byte[] fnumberVal = AmberProperty.encodeBlob(fnumber);
        byte[] dnumberVal = AmberProperty.encodeBlob(dnumber);
        byte[] dateVal = AmberProperty.encodeBlob(date);
        
        assertNotNull(strVal);
        assertNotNull(numberVal);
        assertNotNull(lnumberVal);
        assertNotNull(boolVal);
        assertNotNull(fnumberVal);
        assertNotNull(dnumberVal);
        assertNotNull(dateVal);
        
        assertEquals(AmberProperty.decodeBlob(strVal, DataType.STR), str);
        assertEquals(AmberProperty.decodeBlob(numberVal, DataType.INT), number);
        assertEquals(AmberProperty.decodeBlob(lnumberVal, DataType.LNG), lnumber);
        assertEquals(AmberProperty.decodeBlob(boolVal, DataType.BLN), bool);
        assertEquals(AmberProperty.decodeBlob(fnumberVal, DataType.FLT), fnumber);
        assertEquals(AmberProperty.decodeBlob(dnumberVal, DataType.DBL), dnumber);
        assertEquals(AmberProperty.decodeBlob(dateVal, DataType.DTE), date);
        
    }
}
