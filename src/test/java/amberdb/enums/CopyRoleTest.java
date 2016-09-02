package amberdb.enums;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import amberdb.model.Copy;
import amberdb.model.Work;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class CopyRoleTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void shouldReturnCopyRolesAlphabetically() {
        List<CopyRole> roles = CopyRole.listAlphabetically();
        for (int i=0; i<roles.size()-1; i++) {
            compareItemsBefore(i, roles);
            compareItemsAfter(i, roles);         
        }                
    }
    
    @Test
    public void shouldReturnOrderedCopiesBaseOnCopyRoles() {
        Work work = amberSession.addWork();
        Copy master = work.addCopy();
        master.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy original = work.addCopy();
        original.setCopyRole(CopyRole.ORIGINAL_COPY.code());
        Copy transcript = work.addCopy();
        transcript.setCopyRole(CopyRole.TRANSCRIPT_COPY.code());
        Copy analogueDistribution = work.addCopy();
        analogueDistribution.setCopyRole(CopyRole.ANALOGUE_DISTRIBUTION_COPY.code());
        Copy working = work.addCopy();
        working.setCopyRole(CopyRole.WORKING_COPY.code());
        Copy listening1 = work.addCopy();
        listening1.setCopyRole(CopyRole.LISTENING_1_COPY.code());
        Copy summary = work.addCopy();
        summary.setCopyRole(CopyRole.SUMMARY_COPY.code());
        Copy relatedMetadata = work.addCopy();
        relatedMetadata.setCopyRole(CopyRole.RELATED_METADATA_COPY.code());
        Copy listening2 = work.addCopy();
        listening2.setCopyRole(CopyRole.LISTENING_2_COPY.code());
        Copy listening3 = work.addCopy();
        listening3.setCopyRole(CopyRole.LISTENING_3_COPY.code());
        Copy digitalDistribution = work.addCopy();
        digitalDistribution.setCopyRole(CopyRole.DIGITAL_DISTRIBUTION_COPY.code());
        Copy[] copies = new Copy[11];
        CopyRole.reorderCopyList(work.getCopies()).toArray(copies);
        Assert.assertEquals("o", copies[0].getCopyRole());
        Assert.assertEquals("m", copies[1].getCopyRole());
        Assert.assertEquals("d", copies[2].getCopyRole());
        Assert.assertEquals("rm", copies[3].getCopyRole());
        Assert.assertEquals("s", copies[4].getCopyRole());
        Assert.assertEquals("tr", copies[5].getCopyRole());
        Assert.assertEquals("l1", copies[6].getCopyRole());
        Assert.assertEquals("l2", copies[7].getCopyRole());
        Assert.assertEquals("l3", copies[8].getCopyRole());
        Assert.assertEquals("w", copies[9].getCopyRole());
        Assert.assertEquals("ad", copies[10].getCopyRole());
    }
    
    @Test
    public void validateCopyRoleEnum() {
        Assert.assertEquals(0,CopyRole.ORIGINAL_COPY.ord());
        Assert.assertEquals(1,CopyRole.MASTER_COPY.ord());
        Assert.assertEquals(2,CopyRole.DERIVATIVE_MASTER_COPY.ord());
        Assert.assertEquals(3,CopyRole.CO_MASTER_COPY.ord());
        Assert.assertEquals(4,CopyRole.DIGITAL_DISTRIBUTION_COPY.ord());
        Assert.assertEquals(5,CopyRole.RELATED_METADATA_COPY.ord());
        Assert.assertEquals(6,CopyRole.SUMMARY_COPY.ord());
        Assert.assertEquals(7,CopyRole.TRANSCRIPT_COPY.ord());
        Assert.assertEquals(8,CopyRole.LISTENING_1_COPY.ord());
        Assert.assertEquals(9,CopyRole.LISTENING_2_COPY.ord());
        Assert.assertEquals(10,CopyRole.LISTENING_3_COPY.ord());
        Assert.assertEquals(11,CopyRole.WORKING_COPY.ord());
        Assert.assertEquals(12,CopyRole.ANALOGUE_DISTRIBUTION_COPY.ord());
        Assert.assertEquals(130,CopyRole.ACCESS_COPY.ord());
        Assert.assertEquals(140,CopyRole.ARCHIVE_COPY.ord());
        Assert.assertEquals(150,CopyRole.EDITED_COPY.ord());
        Assert.assertEquals(160,CopyRole.ELECTRONIC_SUMMARY.ord());
        Assert.assertEquals(170,CopyRole.ELECTRONIC_TRANSCRIPT.ord());
        Assert.assertEquals(180,CopyRole.EXAMINATION_COPY.ord());
        Assert.assertEquals(190,CopyRole.FILTERED_COPY.ord());
        Assert.assertEquals(200,CopyRole.FINDING_AID_COPY.ord());
        Assert.assertEquals(201,CopyRole.FINDING_AID_SUPPLEMENTARY_COPY.ord());
        Assert.assertEquals(210,CopyRole.FINDING_AID_PRINT_COPY.ord());
        Assert.assertEquals(220,CopyRole.FINDING_AID_VIEW_COPY.ord());
        Assert.assertEquals(230,CopyRole.FLIGHT_DIAGRAM_COPY.ord());
        Assert.assertEquals(240,CopyRole.IMAGE_PACKAGE.ord());
        Assert.assertEquals(250,CopyRole.INDEX_COPY.ord());
        Assert.assertEquals(260,CopyRole.LIST_COPY.ord());
        Assert.assertEquals(269,CopyRole.MASTER_ANALOGUE_COPY.ord());
        Assert.assertEquals(270,CopyRole.MICROFORM_COPY.ord());
        Assert.assertEquals(280,CopyRole.OCR_METS_COPY.ord());
        Assert.assertEquals(290,CopyRole.OCR_ALTO_COPY.ord());
        Assert.assertEquals(300,CopyRole.OCR_JSON_COPY.ord());
        Assert.assertEquals(310,CopyRole.PAPER_SUMMARY.ord());
        Assert.assertEquals(320,CopyRole.PAPER_TRANSCRIPT.ord());
        Assert.assertEquals(330,CopyRole.PRINT_COPY.ord());
        Assert.assertEquals(340,CopyRole.PRODUCTION_MASTER_AUDIO_LEFT_COPY.ord());
        Assert.assertEquals(341,CopyRole.PRODUCTION_MASTER_AUDIO_RIGHT_COPY.ord());
        Assert.assertEquals(342,CopyRole.PRODUCTION_MASTER_VIDEO_COPY.ord());
        Assert.assertEquals(350,CopyRole.QUICKTIME_FILE_1_COPY.ord());
        Assert.assertEquals(360,CopyRole.QUICKTIME_FILE_2_COPY.ord());
        Assert.assertEquals(370,CopyRole.QUICKTIME_REF_1_COPY.ord());
        Assert.assertEquals(380,CopyRole.QUICKTIME_REF_2_COPY.ord());
        Assert.assertEquals(390,CopyRole.QUICKTIME_REF_3_COPY.ord());
        Assert.assertEquals(400,CopyRole.QUICKTIME_REF_4_COPY.ord());
        Assert.assertEquals(410,CopyRole.REAL_MEDIA_FILE_COPY.ord());
        Assert.assertEquals(420,CopyRole.REAL_MEDIA_REF_COPY.ord());
        Assert.assertEquals(430,CopyRole.RTF_TRANSCRIPT.ord());
        Assert.assertEquals(439,CopyRole.SECOND_COPY.ord());
        Assert.assertEquals(440,CopyRole.SPECIAL_DELIVERY_COPY.ord());
        Assert.assertEquals(450,CopyRole.STRUCTURAL_MAP_COPY.ord());
        Assert.assertEquals(460,CopyRole.THUMBNAIL_COPY.ord());
        Assert.assertEquals(470,CopyRole.TIME_CODED_SUMMARY.ord());
        Assert.assertEquals(480,CopyRole.TIME_CODED_TRANSCRIPT_COPY.ord());
        Assert.assertEquals(482,CopyRole.TRANSCRIPT_DELIVERY_COPY.ord());
        Assert.assertEquals(485,CopyRole.UNKNOWN_COPY.ord());
        Assert.assertEquals(490,CopyRole.VIEW_COPY.ord());
        Assert.assertEquals(500,CopyRole.VISUAL_NAVIGATION_DELIVERY_COPY.ord());
        Assert.assertEquals(58,CopyRole.values().length);
    }
    
    private void compareItemsBefore(int index, List<CopyRole> roles) {        
        CopyRole r1 = roles.get(index);        
        for (int i=0; i<index-1; i++) {            
            CopyRole r2 = roles.get(i);
            int result = r1.display().compareTo(r2.display());            
            Assert.assertTrue(result >= 0);
        }             
    }
    
    private void compareItemsAfter(int index, List<CopyRole> roles) {        
        CopyRole r1 = roles.get(index);        
        for (int i=index+1; i<roles.size(); i++) {            
            CopyRole r2 = roles.get(i);
            int result = r1.display().compareTo(r2.display());            
            Assert.assertTrue(result <= 0);
        }             
    }

}
