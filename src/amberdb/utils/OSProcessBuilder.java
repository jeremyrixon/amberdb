package amberdb.utils;

import java.nio.file.Path;

public class OSProcessBuilder {
    private Path osCmd;
    private Path input;
    private Path output;
    private Options options;
    
    public OSProcessBuilder(String... args) {
        this.options = new Options(args);
    }
    
    public OSProcessBuilder setCmdPath(Path osCmd) {
        this.osCmd = osCmd;
        return this;
    }
    
    public OSProcessBuilder setInputPath(Path input) {
        this.input = input;
        return this;
    }
    
    public OSProcessBuilder setOutputPath(Path output) {
        this.output = output;
        return this;
    }
    
    public ProcessBuilder assemble() {
        if (options == null) return null;
        
        options.setCmdPath(osCmd.toString());
        options.setInput(input.toString());
        options.setOutput(output.toString());

        return new ProcessBuilder(options.asArray());
    }
}
