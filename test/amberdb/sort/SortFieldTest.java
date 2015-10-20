package amberdb.sort;


import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import amberdb.model.Work;

public class SortFieldTest {

    private Work work1, work2, work3, work4, work5;
    
    @Before
    public void setup(){
        work1 = mock(Work.class);
        work2 = mock(Work.class);
        work3 = mock(Work.class);
        work4 = mock(Work.class);
        work5 = mock(Work.class);
    }
    
    @Test
    public void aliasSort() throws Exception{
        when(work1.getAlias()).thenReturn(new ArrayList<String>());
        when(work2.getAlias()).thenReturn(Arrays.asList("abc002"));
        when(work3.getAlias()).thenReturn(Arrays.asList("abc001"));
        when(work4.getAlias()).thenReturn(null);
        when(work5.getAlias()).thenReturn(Arrays.asList("abc004", "abc000"));
        List<Work> work = new ArrayList<>(Arrays.asList(work1, work2, work3, work4, work5));
        Collections.sort(work, SortField.ALIAS.ascComparator);
        assertThat(work.get(0).getAlias(), hasItem("abc001"));
        assertThat(work.get(2).getAlias(), hasItem("abc004"));
        assertThat(CollectionUtils.isEmpty(work.get(3).getAlias()), is(true));
        assertThat(CollectionUtils.isEmpty(work.get(4).getAlias()), is(true));
        Collections.sort(work, SortField.ALIAS.descComparator);
        assertThat(work.get(0).getAlias(), hasItem("abc004"));
        assertThat(work.get(2).getAlias(), hasItem("abc001"));
        assertThat(CollectionUtils.isEmpty(work.get(3).getAlias()), is(true));
        assertThat(CollectionUtils.isEmpty(work.get(4).getAlias()), is(true));
    }
    

}
