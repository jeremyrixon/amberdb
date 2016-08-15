package amberdb;

import org.junit.Assert;
import org.junit.Test;

public class PIUtilTest {

    @Test
    public void testCreateRightCheckDigit() {
        Integer expected = 4;
        long input = 572L;
        Assert.assertEquals("The check digit of " + input + " should be " + expected + ".", expected, PIUtil.taq(input));
    }

    @Test
    public void testReturnValidPI() {
        String expected = "nla.obj-5724";
        long input = 572L;
        Assert.assertEquals("The returned PI for obj id " + input + " should be " + expected + ".", expected, PIUtil.format(input));
        Assert.assertTrue("The returned PI " + PIUtil.format(input) + " should be valid.", PIUtil.isValid(PIUtil.format(input)));
    }

    @Test
    public void testDetectInvalidPI() {
        String input = "nla.obj-5723";
        Assert.assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "5723";
        Assert.assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "nla.obj-";
        Assert.assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "nla.obj-fgsfds";
        Assert.assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "!!!Are Emus valid PIs???";
        Assert.assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));
    }

    @Test
    public void testReturnValidObjId() {
        Long expected = 572L;
        String input = "nla.obj-5724";
    }

    @Test(expected = InvalidObjectIDException.class)
    public void testReturnExceptionForInvalidObjId() {
        PIUtil.parse("nla.obj-5723");
    }

    @Test(expected = InvalidObjectIDException.class)
    public void testCheckDigitsForNull() {
        PIUtil.taq(null);
    }
}
