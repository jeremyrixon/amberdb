package amberdb.utils.images;

import java.nio.file.Path;

import amberdb.utils.OSProcessBuilder;

public class JP2EchoBuilder extends OSProcessBuilder {
    private static String[] options = { "echo", "generating jp2 from", "$input", "to", "$output"};
    public JP2EchoBuilder() {
        super(options);
    }
    
    @Override
    public OSProcessBuilder setCmdPath(Path osCmd) {
        return this;
    }
}
