package amberdb.utils.images;

import amberdb.utils.OSProcessBuilder;

public class TiffCPBuilder extends OSProcessBuilder {
    private static String[] options = { "tiffcp", "-c none", "$input", "$output"};
    public TiffCPBuilder() {
        super(options);
    }
}
