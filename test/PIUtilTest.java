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
        assertEquals("The returned PI for obj id " + input + " should be " + expected + ".", expected, PIUtil.pi(input));
        assertTrue("The returned PI " + PIUtil.pi(input) + " should be valid.", PIUtil.isValidPI(PIUtil.pi(input)));
    }
    
    @Test
    public void testDetectInvalidPI() {
        String input = "nla.obj-5723";
        assertFalse("The input PI " + input + " is invalid.", PIUtil.isValidPI(input));
    }
}
