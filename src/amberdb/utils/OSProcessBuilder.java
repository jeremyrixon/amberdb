package amberdb.utils;

import java.nio.file.Path;

public class OSProcessBuilder {
    private Path osCmd = null;
    private Path input = null;
    private Path output = null;
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
    
    public Options getOptions() {
        return options;
    }
    
    public ProcessBuilder assemble() {
        if (options == null) return null;
        
        if (osCmd != null) options.setCmdPath(osCmd.toString());
        if (input != null) options.setInput(input.toString());
        if (output != null) options.setOutput(output.toString());

        System.out.println("assembled command: " + options.toString());
        return new ProcessBuilder(options.asArray());
    }
}
