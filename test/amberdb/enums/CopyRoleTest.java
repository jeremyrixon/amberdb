package amberdb.enums;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CopyRoleTest {

    @Test
    public void shouldReturnCopyRolesAlphabetically() {
        List<CopyRole> roles = CopyRole.listAlphabetically();       
        for (int i=0; i<roles.size()-1; i++) {
            compareItemsBefore(i, roles);
            compareItemsAfter(i, roles);         
        }                
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
