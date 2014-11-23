package amberdb.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.junit.Test;

public class PdfTransformerTest {
    @Test
    public void testGeneratePdf() throws IOException {
        Tika tika = new Tika();
        InputStream in = new FileInputStream(new File("test/resources/books.xml"));
        Path[] stylesheets = { Paths.get("test/resources/books.xsl") };
        byte[] pdf = PdfTransformerFop.transform(in, stylesheets);
        assertTrue(tika.detect(pdf).equals("application/pdf"));
    }
}
