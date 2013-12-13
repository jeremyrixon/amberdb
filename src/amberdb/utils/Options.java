package amberdb.utils;

public class Options {
    String[] options;
    int inputArgIdx = -1;
    int outputArgIdx = -1;
    
    public Options(String... options) {
        this.options = options;
        if (options != null) {
            int i = 0;
            for (String option : options) {
                if (option.equalsIgnoreCase("$input")) 
                    inputArgIdx = i;
                else if (option.equalsIgnoreCase("$output"))
                    outputArgIdx = i;
                i++;
            }
        }
    }
    
    public void setCmdPath(String cmdPath) {
        options[0] = cmdPath;
    }
    
    public void setInput(String input) {
        if (inputArgIdx > -1) {
            options[inputArgIdx] = input;
        }
    }
    
    public void setOutput(String output) {
        if (outputArgIdx > -1) {
            options[outputArgIdx] = output;
        }
    }
    
    public String[] asArray() {
        return options;
    }
    
    public String toString() {
        if (options == null) return "";
        
        String cmd = "";
        for (String option : options) {
            cmd += option + " ";
        }
        return cmd;
    }
}
