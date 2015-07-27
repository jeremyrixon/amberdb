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
