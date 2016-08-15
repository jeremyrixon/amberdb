package amberdb.graph;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AmberPropertyTest {

    private static String str;
    private static Integer number;
    private static Long lnumber;
    private static Boolean bool;
    private static Float fnumber;
    private static Double dnumber;
    private static Date date;
    
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
    }
    
    @Test
    public void testEncodeDecodeAmberDataTypes() {
        byte[] strVal = AmberProperty.encode(str);
        byte[] numberVal = AmberProperty.encode(number);
        byte[] lnumberVal = AmberProperty.encode(lnumber);
        byte[] boolVal = AmberProperty.encode(bool);
        byte[] fnumberVal = AmberProperty.encode(fnumber);
        byte[] dnumberVal = AmberProperty.encode(dnumber);
        byte[] dateVal = AmberProperty.encode(date);
        
        Assert.assertNotNull(strVal);
        Assert.assertNotNull(numberVal);
        Assert.assertNotNull(lnumberVal);
        Assert.assertNotNull(boolVal);
        Assert.assertNotNull(fnumberVal);
        Assert.assertNotNull(dnumberVal);
        Assert.assertNotNull(dateVal);
        
        Assert.assertEquals(AmberProperty.decode(strVal, DataType.STR), str);
        Assert.assertEquals(AmberProperty.decode(numberVal, DataType.INT), number);
        Assert.assertEquals(AmberProperty.decode(lnumberVal, DataType.LNG), lnumber);
        Assert.assertEquals(AmberProperty.decode(boolVal, DataType.BLN), bool);
        Assert.assertEquals(AmberProperty.decode(fnumberVal, DataType.FLT), fnumber);
        Assert.assertEquals(AmberProperty.decode(dnumberVal, DataType.DBL), dnumber);
        Assert.assertEquals(AmberProperty.decode(dateVal, DataType.DTE), date);
        
    }
}
