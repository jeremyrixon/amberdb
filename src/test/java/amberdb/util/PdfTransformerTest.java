package amberdb.util;

import org.apache.tika.Tika;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;

public class PdfTransformerTest {
    @Test
    public void testGeneratePdf() throws IOException {
        Tika tika = new Tika();
        InputStream in = new FileInputStream(new File("src/test/resources/books.xml"));
        Reader stylesheets = new FileReader(Paths.get("src/test/resources/books.xsl").toFile());
        byte[] pdf = PdfTransformerFop.transform(in, stylesheets);
        Assert.assertTrue(tika.detect(pdf).equals("application/pdf"));
    }
}
