package amberdb.util;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.ImageFile;
import amberdb.model.Page;
import amberdb.model.Work;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OcrCheckTest extends AbstractDatabaseIntegrationTest {
    private Page blinkyBillMaster;
    private Page blinkyBillCoMaster;
    java.io.File jsonFile = Paths.get("src/test/resources/nla.obj-20673-oc.json").toFile();
    
    @Before
    public void init() throws IOException, ParseException {
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd-MMM-yyyy");
        Work book1 = amberSession.addWork();
        blinkyBillMaster = book1.addPage();
        Copy[] copies = new Copy[3];
        copies[0] = blinkyBillMaster.addCopy(Paths.get("src/test/resources/nla.obj-20673-m.tif"), CopyRole.MASTER_COPY, "image/tiff");
        copies[0].getImageFile().setFileName("nla.obj-20673-m.tif");
        
        Work book2 = amberSession.addWork();
        blinkyBillCoMaster = book2.addPage();
        copies[1] = blinkyBillCoMaster.addCopy(Paths.get("src/test/resources/nla.obj-20673-m.tif"), CopyRole.MASTER_COPY, "image/tiff");
        copies[2] = blinkyBillCoMaster.addCopy(Paths.get("src/test/resources/nla.obj-20673-c.tif"), CopyRole.CO_MASTER_COPY, "image/tiff");
        copies[1].getImageFile().setFileName("nla.obj-20673-m.tif");
        copies[2].getImageFile().setFileName("nla.obj-20673-c.tif");
        for (Copy copy : copies) {
            ImageFile file = copy.getImageFile();
            file.setResolution("300x300");
            file.setColourSpace("RGB");
            file.setImageWidth(2188);
            file.setImageLength(2905);
            file.setBitDepth("8,8,8");
            file.setApplicationDateCreated(dateFmt.parse("04-APR-2014"));
        }
        blinkyBillCoMaster.addCopy(jsonFile.toPath(), CopyRole.OCR_JSON_COPY, "application/json");
        amberSession.commit();
    }
    
    @Test
    public void testOcrOutOfBound() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        OcrCheck ocrChk = new OcrCheck(blinkyBillMaster);
        
        // test ocrOutOfBound(int x, int y, int w, int h)
        boolean outOfBound1 = ocrChk.ocrOutOfBound(2189, 300, 400, 450);
        Assert.assertTrue("Ocr is out of bound", outOfBound1);
        boolean outOfBound2 = ocrChk.ocrOutOfBound(0, 2188, 400, 450);
        Assert.assertFalse("Ocr is out of bound", outOfBound2);
        
        // test ocrOutOfBound(java.io.File jsonFile)
        boolean outOfBound3 = ocrChk.ocrOutOfBound(jsonFile);
        Assert.assertTrue("Ocr is out of bound", outOfBound3);
        
        // test ocrOutOfBound()
        OcrCheck ocrChk1 = new OcrCheck(blinkyBillCoMaster);
        boolean outOfBound4 = ocrChk1.ocrOutOfBound();
        Assert.assertTrue("Ocr is out of bound", outOfBound4);
    }
    
    @Test
    public void testGetImage() {
        // test get image from co-master copy if exist
        ImageFile image1 = new OcrCheck(blinkyBillCoMaster).getImage();
        Assert.assertTrue("Image file is from the co-master copy", image1.getFileName().endsWith("-c.tif"));
        
        // test get image from master copy if only master copy exist
        ImageFile image2 = new OcrCheck(blinkyBillMaster).getImage();
        Assert.assertTrue("Image file is from the master copy", image2.getFileName().endsWith("-m.tif"));
    }
}
