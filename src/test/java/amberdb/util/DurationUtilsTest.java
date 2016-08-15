package amberdb.util;

import org.junit.Assert;
import org.junit.Test;

public class DurationUtilsTest {

    @Test
    public void testConvertDuration(){
        String newDuration = DurationUtils.convertDuration("1:12:3:4");
        Assert.assertEquals("01:12:03", newDuration);
        newDuration = DurationUtils.convertDuration("12:3:4");
        Assert.assertEquals("12:03:04", newDuration);
        newDuration = DurationUtils.convertDuration("0:3:4");
        Assert.assertEquals("00:03:04", newDuration);
        newDuration = DurationUtils.convertDuration("invalid");
        Assert.assertEquals("invalid", newDuration);
        newDuration = DurationUtils.convertDuration(null);
        Assert.assertNull(newDuration);
    }
    
    @Test
    public void testConvertSecondsToDuration(){
        String newDuration = DurationUtils.convertDurationFromSeconds((float)3747);
        Assert.assertEquals("01:02:27", newDuration);
    }
}
