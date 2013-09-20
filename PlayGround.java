public class PlayGround {
    static class Job {
        List<File> files;
    }


    void uploadFiles(Job job) {
        List<File> list = new ArrayList<File>();
        list.add(new File("/tmp/a.tiff"));
        list.add(new File("/tmp/b.tiff"));

        list.add(new File("/tmp/nla.aus-vn12345-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345-2.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345.xml"));

        list.add(new File("/tmp/nla.aus-vn643643-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn643643-2.tiff"));

        job.files = list;
    }

    void identifyWorks(Job job) {
        try (AmberDb amberDb = new AmberDb("jdbc:amber")) {

            // recover existing transaction if any
            // NOTE: this recover and the corresponding suspend
            // could be done in a common pre-action and post-action
            // controller method
            if (job.getAmberTxId() != null) {
                amberDb.recover(job.getAmberTxId());
            }

            List<Work> works;
            // try to detect works based on filenames,

            Section auto1 = amberDb.addSection();

            auto1.setBibId(12345L);

            Page page = amberDb.addPageTo(auto1);

            amberDb.addImageTiffCopyTo(page, job.files.get(3));
            amberDb.addOCRMETSCopyTo(page, job.files.get(5));

            amberDb.addPageTo(auto1, job.files.get(4));

            auto1.setTitle("Blinky Bill");
            works.add(auto1.getId());


            Section auto2 = amberDb.addSection();
            auto2.setBibId(55555);
            amberDb.addPageTo(auto2, job.files.get(6));
            amberDb.addPageTo(auto2, job.files.get(7));
            auto2.setTitle("James and the giant peach");

            works.add(auto2.getId());


            // user manually creates a work out of the first two pages

            Section manual = amberDb.addSection();
            amberDb.addPageTo(manual, job.files.get(1));
            amberDb.addPageTo(manual, job.files.get(2));
            manual.setTitle("Little red riding hood");

            works.add(manual.getId());

            job.works = works;

            // save this transaction without committing it
            // so we can continue manipulating these objects
            // in another web request
            job.setAmberTxId(amberDb.suspend());
        }
    }


    void qaWorks(Job job) {
        // recover existing transaction if any
        if (job.getAmberTxId() != null) {
            amberDb.recover(job.getAmberTxId());
        }

        for (Long workId: job.getWorks()) {
            qaWork(amberDb.getWork(workId));
        }

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(amberDb.suspend());
    }

    // gets run 3 times
    void qaWork(Job job, Section section) {

        // fill in default values

        for (Page p: section.getPages()) {
            page.setDevice(job.getDefaultDevice());
            page.setSoftware(job.getDefaultSoftware());
        }

        // show the qa form
        section.swapPages(1,2);
        Page page = work.getPage(1);
        page.rotate(10);
        page.crop(100,100,200,200);
        page.setTitle("IV");


    }

       // would be called when the user wants to save (eg at the "send to docworks" stage)
    void completeJob(Job job) {
        // recover existing transaction if any
        amberDb.recover(job.getAmberTxId());
        amberDb.commit();
    }


    void cancelJob(Job job) {
        amberDb.rollback();
    }

        // returning later and editing an existing object outside of a job
    void fixLabel() {
        Section section = amberDb.findSection(1234L);

        Page page7 = section.getPage(7);
        page.setTitle("III");

        amberDb.commit();
    }
}
