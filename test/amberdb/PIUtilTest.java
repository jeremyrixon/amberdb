package amberdb;
import static org.junit.Assert.*;

import org.junit.Test;

import amberdb.PIUtil;


public class PIUtilTest {

    @Test
    public void testCreateRightCheckDigit() {
        int expected = 4;
        long input = 572L;
        assertEquals("The check digit of " + input + " should be " + expected + ".", expected, PIUtil.taq(input));
    }

    @Test
    public void testReturnValidPI() {
        String expected = "nla.obj-5724";
        long input = 572L;
        assertEquals("The returned PI for obj id " + input + " should be " + expected + ".", expected, PIUtil.format(input));
        assertTrue("The returned PI " + PIUtil.format(input) + " should be valid.", PIUtil.isValid(PIUtil.format(input)));
    }
    
    @Test
    public void testDetectInvalidPI() {
        String input = "nla.obj-5723";
        assertFalse("The input PI " + input + " is invalid.", PIUtil.isValid(input));
    }
    
    @Test
    public void testReturnValidObjId() {
        long expected = 572;
        String input = "nla.obj-5724";
        assertEquals("The returned objId should be " + expected + ".", expected, PIUtil.parse(input));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testReturnExceptionForInvalidObjId() {
        PIUtil.parse("nla.obj-5723");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCheckDigitsForNull() {
        PIUtil.taq(null);
    }
}
