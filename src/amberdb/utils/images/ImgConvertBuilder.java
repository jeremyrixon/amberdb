package amberdb.utils.images;

import amberdb.utils.OSProcessBuilder;

public class ImgConvertBuilder extends OSProcessBuilder {
    private static String[] options = { "convert", "$input", "+compress", "$output"};
    public ImgConvertBuilder() {
        super(options);
    }
}
