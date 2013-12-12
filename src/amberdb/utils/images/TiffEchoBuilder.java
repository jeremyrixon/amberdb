package amberdb.utils.images;

import java.nio.file.Path;

import amberdb.utils.OSProcessBuilder;

public class TiffEchoBuilder extends OSProcessBuilder {
    private static String[] options = { "echo", "uncompressing", "$input", "to", "$output", "..."};
    public TiffEchoBuilder() {
        super(options);
    }
    
    @Override
    public OSProcessBuilder setCmdPath(Path osCmd) {
        return this;
    }
}
