package amberdb.utils.images;

import amberdb.utils.OSProcessBuilder;

public class KduCompressBuilder extends OSProcessBuilder {
    private static String[] options = { "kdu_compress", "-i", "$input", "-o", "$output", "-rate 0.5", "Clayers=1", "Clevels=7", 
                                        "'Cprecincts={256,256},{256,256},{256,256},{128,128},{128,128},{64,64},{64,64},{32,32},{16,16}',",
                                        "'Corder=RPCL'", "'ORGgen_plt=yes'", "'Cblk={32,32}'", "Cuse_sop=yes"};
    public KduCompressBuilder() {
        super(options);
    }
}
