package amberdb.enums;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberSession;
import amberdb.model.Copy;
import amberdb.model.Work;

public class CopyRoleTest {
    private AmberSession amberDb;

    @Before
    public void startup() {
        amberDb = new AmberSession();  
    }
    
    @After
    public void teardown() throws IOException {
        if (amberDb != null) {
            amberDb.close();
        }
    }
    
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
        Work work = amberDb.addWork();
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
        assertEquals("o", copies[0].getCopyRole());
        assertEquals("m", copies[1].getCopyRole());
        assertEquals("d", copies[2].getCopyRole());
        assertEquals("rm", copies[3].getCopyRole());
        assertEquals("s", copies[4].getCopyRole());
        assertEquals("tr", copies[5].getCopyRole());
        assertEquals("l1", copies[6].getCopyRole());
        assertEquals("l2", copies[7].getCopyRole());
        assertEquals("l3", copies[8].getCopyRole());
        assertEquals("w", copies[9].getCopyRole());
        assertEquals("ad", copies[10].getCopyRole());
    }
    
    @Test
    public void validateCopyRoleEnum() {
        assertEquals(0,CopyRole.ORIGINAL_COPY.ord());
        assertEquals(1,CopyRole.MASTER_COPY.ord());
        assertEquals(2,CopyRole.DERIVATIVE_MASTER_COPY.ord());
        assertEquals(3,CopyRole.CO_MASTER_COPY.ord());
        assertEquals(4,CopyRole.DIGITAL_DISTRIBUTION_COPY.ord());
        assertEquals(5,CopyRole.RELATED_METADATA_COPY.ord());
        assertEquals(6,CopyRole.SUMMARY_COPY.ord());
        assertEquals(7,CopyRole.TRANSCRIPT_COPY.ord());
        assertEquals(8,CopyRole.LISTENING_1_COPY.ord());
        assertEquals(9,CopyRole.LISTENING_2_COPY.ord());
        assertEquals(10,CopyRole.LISTENING_3_COPY.ord());
        assertEquals(11,CopyRole.WORKING_COPY.ord());
        assertEquals(12,CopyRole.ANALOGUE_DISTRIBUTION_COPY.ord());
        assertEquals(13,CopyRole.ACCESS_COPY.ord());
        assertEquals(14,CopyRole.ARCHIVE_COPY.ord());
        assertEquals(15,CopyRole.EDITED_COPY.ord());
        assertEquals(16,CopyRole.ELECTRONIC_SUMMARY.ord());
        assertEquals(17,CopyRole.ELECTRONIC_TRANSCRIPT.ord());
        assertEquals(18,CopyRole.EXAMINATION_COPY.ord());
        assertEquals(19,CopyRole.FILTERED_COPY.ord());
        assertEquals(20,CopyRole.FINDING_AID_COPY.ord());
        assertEquals(21,CopyRole.FINDING_AID_PRINT_COPY.ord());
        assertEquals(22,CopyRole.FINDING_AID_VIEW_COPY.ord());
        assertEquals(23,CopyRole.FINDING_AID_1_COPY.ord());
        assertEquals(24,CopyRole.FINDING_AID_2_COPY.ord());
        assertEquals(25,CopyRole.FINDING_AID_3_COPY.ord());
        assertEquals(26,CopyRole.FINDING_AID_4_COPY.ord());
        assertEquals(27,CopyRole.FINDING_AID_5_COPY.ord());
        assertEquals(28,CopyRole.FINDING_AID_6_COPY.ord());
        assertEquals(29,CopyRole.FINDING_AID_7_COPY.ord());
        assertEquals(30,CopyRole.FINDING_AID_8_COPY.ord());
        assertEquals(31,CopyRole.FINDING_AID_9_COPY.ord());
        assertEquals(32,CopyRole.FINDING_AID_10_COPY.ord());
        assertEquals(33,CopyRole.IMAGE_PACKAGE.ord());
        assertEquals(34,CopyRole.LIST_COPY.ord());
        assertEquals(35,CopyRole.MICROFORM_COPY.ord());
        assertEquals(36,CopyRole.OCR_METS_COPY.ord());
        assertEquals(37,CopyRole.OCR_ALTO_COPY.ord());
        assertEquals(38,CopyRole.OCR_JSON_COPY.ord());
        assertEquals(39,CopyRole.PAPER_SUMMARY.ord());
        assertEquals(40,CopyRole.PAPER_TRANSCRIPT.ord());
        assertEquals(41,CopyRole.PRINT_COPY.ord());
        assertEquals(42,CopyRole.PRODUCTION_MATERIAL.ord());
        assertEquals(43,CopyRole.QUICKTIME_FILE_1_COPY.ord());
        assertEquals(44,CopyRole.QUICKTIME_FILE_2_COPY.ord());
        assertEquals(45,CopyRole.QUICKTIME_REF_1_COPY.ord());
        assertEquals(46,CopyRole.QUICKTIME_REF_2_COPY.ord());
        assertEquals(47,CopyRole.QUICKTIME_REF_3_COPY.ord());
        assertEquals(48,CopyRole.QUICKTIME_REF_4_COPY.ord());
        assertEquals(49,CopyRole.REAL_MEDIA_FILE_COPY.ord());
        assertEquals(50,CopyRole.REAL_MEDIA_REF_COPY.ord());
        assertEquals(51,CopyRole.RTF_TRANSCRIPT.ord());
        assertEquals(52,CopyRole.SPECIAL_DELIVERY_COPY.ord());
        assertEquals(53,CopyRole.STRUCTURAL_MAP_COPY.ord());
        assertEquals(54,CopyRole.THUMBNAIL_COPY.ord());
        assertEquals(55,CopyRole.TIME_CODED_SUMMARY.ord());
        assertEquals(56,CopyRole.TIME_CODED_TRANSCRIPT_COPY.ord());
        assertEquals(57,CopyRole.VIEW_COPY.ord());
        assertEquals(58,CopyRole.values().length);
    }
    
    private void compareItemsBefore(int index, List<CopyRole> roles) {        
        CopyRole r1 = roles.get(index);        
        for (int i=0; i<index-1; i++) {            
            CopyRole r2 = roles.get(i);
            int result = r1.display().compareTo(r2.display());            
            assertTrue(result >= 0);            
        }             
    }
    
    private void compareItemsAfter(int index, List<CopyRole> roles) {        
        CopyRole r1 = roles.get(index);        
        for (int i=index+1; i<roles.size(); i++) {            
            CopyRole r2 = roles.get(i);
            int result = r1.display().compareTo(r2.display());            
            assertTrue(result <= 0);            
        }             
    }

}
