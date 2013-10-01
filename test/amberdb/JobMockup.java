package amberdb;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class JobMockup {
    int txId;
    List<Path> files;
    List<Long> workIds;

    public Integer getAmberTxId() {
        // TODO Auto-generated method stub
        return txId;
    }
    
    public void setAmberTxId(int txId) {
        this.txId = txId;
    }
    
    public List<Long> getWorks() {
        return workIds;
    }
    
    public String getDefaultDevice() {
        return "device";
    }
    
    public String getDefaultSoftware() {
        return "apps";
    }
}
