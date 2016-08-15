package amber.checksum;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ChecksumAlgorithmTest {

    @Test
    public void fromString() {
        Assert.assertThat(ChecksumAlgorithm.fromString("MD5"), CoreMatchers.is(ChecksumAlgorithm.MD5));
    }
    
    @Test
    public void fromStringLowerCase() {
        Assert.assertThat(ChecksumAlgorithm.fromString("md5"), CoreMatchers.is(ChecksumAlgorithm.MD5));
    }
    
    @Test
    public void fromStringNoHash() {
        Assert.assertThat(ChecksumAlgorithm.fromString("SHA1"), CoreMatchers.is(ChecksumAlgorithm.SHA1));
    }
    
    @Test
    public void fromStringWithHash() {
        Assert.assertThat(ChecksumAlgorithm.fromString("SHA-1"), CoreMatchers.is(ChecksumAlgorithm.SHA1));
    }

}
