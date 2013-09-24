package amberdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amberdb.AmberDb;
import amberdb.enums.CopyRole;
import amberdb.model.Page;
import amberdb.model.Section;

public class PlayGround {
    public static AmberDb dao;
    public static Job job;
    
    public PlayGround() throws IOException {
        job = uploadFiles();
        dao = identifyWorks();
    }
    
    private Job uploadFiles() {
        job = new Job();
        
        // Note: banjo upload files step does not interact with amberDb at all.
        List<File> list = new ArrayList<File>();
        list.add(new File("/tmp/a.tiff"));
        list.add(new File("/tmp/b.tiff"));

        list.add(new File("/tmp/nla.aus-vn12345-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345-2.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345.xml"));

        list.add(new File("/tmp/nla.aus-vn643643-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn643643-2.tiff"));

        job.files = list;
        return job;
    }
    
    private AmberDb identifyWorks() throws IOException {
        try (AmberDb amberDb = new AmberDb()) {

            // recover existing transaction if any
            // NOTE: this recover and the corresponding suspend
            // could be done in a common pre-action and post-action
            // controller method
            if (job.getAmberTxId() != null) {
                amberDb.recover(job.getAmberTxId());
            }

            List<Long> bookIds = new ArrayList<Long>();
            // try to detect works based on filenames,

            Section auto1 = amberDb.addSection();
            auto1.setBibId(12345L);
            auto1.addPage(job.files.get(6));

            Page page = auto1.addPage();

            page.addCopy(job.files.get(2), CopyRole.MASTER_COPY);
            page.addCopy(job.files.get(4), CopyRole.OCR_METS_COPY);

            auto1.addPage(job.files.get(3));

            auto1.setTitle("Blinky Bill");
            bookIds.add(auto1.getId());


            Section auto2 = amberDb.addSection();
            auto2.setBibId(55555);
            auto2.addPage(job.files.get(5));
            auto2.setTitle("James and the giant peach");

            bookIds.add(auto2.getId());


            // user manually creates a work out of the first two pages

            Section manual = amberDb.addSection();
            manual.addPage(job.files.get(0));
            manual.addPage(job.files.get(1));
            manual.setTitle("Little red riding hood");

            bookIds.add(manual.getId());

            job.workIds = bookIds;

            // save this transaction without committing it
            // so we can continue manipulating these objects
            // in another web request
            job.setAmberTxId(amberDb.suspend());
            
            return amberDb;
        }
    }

    
    static class Job {
        int txId;
        List<File> files;
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
 
}
