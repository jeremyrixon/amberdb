package amberdb.query;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PropertyQueryAssemblerTest {
    @Test
    public void sql(){
        List<WorkProperty> list = new ArrayList<>();
        list.add(new WorkProperty("title", "abc"));
        list.add(new WorkProperty("collection", "nla.pic"));
        list.add(new WorkProperty("recordSource", "voyager"));
        PropertyQueryAssembler assembler = new PropertyQueryAssembler(list);
        assertThat(assembler.sql(), is("select v.id from property p1, property p2, property p3,  vertex v where v.txn_end=0 and p1.txn_end=0 and p2.txn_end=0 and p3.txn_end=0  and v.id=p1.id and p1.id=p2.id and p2.id=p3.id  and p1.name='title' and p1.value=? and p2.name='collection' and p2.value=? and p3.name='recordSource' and p3.value=? "));
    }
}