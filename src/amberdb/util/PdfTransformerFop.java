package amberdb.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

import doss.Writable;
import doss.core.Writables;

public class PdfTransformerFop {
    static final Logger log = LoggerFactory.getLogger(PdfTransformerFop.class);
    
    /**
     * getTool returns the default tool with transformation stylesheets for configuration.
     * @param stylesheets
     * @return the generated PDF.
     * @throws IOException
     */
    public static Function<InputStream, Writable> getTool(final Path... stylesheets) throws IOException {
        return new Function<InputStream, Writable>() {
            @Override
            public Writable apply(InputStream in) {
                FopFactory fopFactory = FopFactory.newInstance();
                ByteArrayOutputStream bas = new ByteArrayOutputStream();
                OutputStream out = new BufferedOutputStream(bas);
                try {
                    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
                    TransformerFactory factory = TransformerFactory.newInstance();
                    for (Path stylesheet : stylesheets) {
                        try {
                            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet.toFile()));
                            Source src = new StreamSource(in);
                            Result res = new SAXResult(fop.getDefaultHandler());
                            transformer.transform(src, res);
                            if (res != null) {
                                return Writables.wrap(bas.toByteArray());
                            }
                        } catch (TransformerException e) {
                            log.info("Tried PDF transformation using stylesheet " + stylesheet.toString() + ", but failed to transform, will try the next stylesheet.");
                        }
                    } 
                } catch (FOPException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        };
    }
}
