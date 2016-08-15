package amberdb.query;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PropertyQueryAssemblerTest {
    @Test
    public void sql(){
        List<WorkProperty> list = new ArrayList<>();
        list.add(new WorkProperty("title", "abc", true));
        list.add(new WorkProperty("collection", "nla.pic"));
        list.add(new WorkProperty("recordSource", "voyager"));
        PropertyQueryAssembler assembler = new PropertyQueryAssembler(list);
        Assert.assertThat(assembler.sql(), CoreMatchers.is("select v.id from property p1, property p2, property p3,  vertex v where v.txn_end=0 " +
                "and p1.txn_end=0 and p2.txn_end=0 and p3.txn_end=0  and v.id=p1.id and p1.id=p2.id and p2.id=p3.id  " +
                "and p1.name='title' and p1.value=? and p2.name='collection' and convert(p2.value using utf8)=? " +
                "and p3.name='recordSource' and convert(p3.value using utf8)=? "));
    }
}