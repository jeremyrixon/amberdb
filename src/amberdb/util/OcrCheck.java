package amberdb.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import amberdb.enums.CopyRole;
import amberdb.model.*;

public class OcrCheck {
    /**
     * ocrOutOfBound: validate the generated ocr json in the jsonFile, and return true if the bounding box for the first paragraph
     *                is not within the corresponding width and length of the image the ocr is generated from. This method can be
     *                called before adding the jsonFile to the work's OCR_JSON_COPY.
     * @param page - the page containing the image the ocr content is generated from.
     * @param jsonFile - the file containing generated ocr content in JSON format.
     * @return indicator whether the generated ocr content is outside the image width and length.
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean ocrOutOfBound(Page page, java.io.File jsonFile) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        if (jsonFile == null) {
            throw new IllegalArgumentException("Input json file is null.");
        }
        if (page == null) {
            throw new IllegalArgumentException("Input page is null.");
        }
        return ocrOutOfBound(page, new FileInputStream(jsonFile));
    }
    
    /**
     * ocrOutOfBound: validate the generated ocr json in the work's ocr json copy, and return true if the bounding box for the first paragraph
     *                is not within the corresponding width and length of the image the ocr is generated from.  This method can be called to
     *                identify issues with existing generated ocr json.
     * @param work - the page containing image and ocr to be compared or the parent work of the page to be checked.
     * @return indicator whether the generated ocr content is outside the image width and length.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public boolean ocrOutOfBound(Work work) throws UnsupportedEncodingException, IOException {
        return ocrOutOfBound(work, null);
    }
    
    private boolean ocrOutOfBound(Work work, InputStream in) throws UnsupportedEncodingException, IOException {
        if (work instanceof Page) {
            Copy ocrJson = work.getCopy(CopyRole.OCR_JSON_COPY);
            Copy pageImg = work.getCopy(CopyRole.CO_MASTER_COPY);
            if (pageImg == null) {
                pageImg = work.getCopy(CopyRole.MASTER_COPY);
            }
            if (ocrJson == null || pageImg == null) {
                return false;
            }

            ImageFile image = pageImg.getImageFile();
            if (image == null) {
                return false;
            }
            
            InputStream ocrStream = in;
            if (ocrStream == null) {
                File ocrJsonFile = ocrJson.getFile();
                if (ocrJsonFile == null) {
                    return false;
                }
                ocrStream = ocrJsonFile.openStream();
            }
            
            Integer imageWidth = image.getImageWidth();
            Integer imageLength = image.getImageLength();
            Integer resolutionX = null;
            String resolution = image.getResolution();
            try {
                if (resolution == null || resolution.isEmpty()) {
                    throw new RuntimeException("Missing image resolution for page " + work.getObjId());
                }
                resolutionX = new Integer(resolution.split("x")[0].replaceAll("[^0-9]+",""));
                if (resolutionX <= 0) {
                    throw new RuntimeException("Invalid image resolution " +  resolution + " for page " + work.getObjId());
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid image resolution " +  resolution + " for page " + work.getObjId());
            }
            
            if (imageWidth == null || imageWidth <= 0) {
                throw new RuntimeException("Missing image width for page " + work.getObjId());
            }
            
            if (imageLength == null || imageLength <= 0) {
                throw new RuntimeException("Missing image length for page " + work.getObjId());
            }
            
            try (InputStreamReader isr = new InputStreamReader(new GZIPInputStream(ocrStream), "utf8")) {
                JsonNode ocr = new ObjectMapper().readTree(isr);
                ArrayNode paragraphs = (ArrayNode) ocr.get("print").get("ps");
                if (paragraphs.size() > 0) {
                    String firstBoundingBox = paragraphs.get(0).get("b").asText();
                    String[] coOrds = firstBoundingBox.split(",");
                    if (coOrds == null || coOrds.length != 4) {
                        throw new RuntimeException("Invalid bounding box " + firstBoundingBox + " in ocr json for work " + work.getObjId());
                    }
                    
                    try {
                        Integer startX = Integer.parseInt(coOrds[0]);
                        Integer startY = Integer.parseInt(coOrds[1]);
                        if (startX <= 0 || startY <=0) {
                            throw new RuntimeException("Invalid ocr bounding box x and y from (x: " + coOrds[0] + ", y: " + coOrds[1] + ")");
                        }
                        if (startX > imageWidth || startY > imageLength) {
                            return true;
                        }
                        
                        Integer widthBound = Integer.parseInt(coOrds[2]);
                        Integer lengthBound = Integer.parseInt(coOrds[3]);
                        if (widthBound <= 0 || lengthBound <=0) {
                            throw new RuntimeException("Invalid ocr bounding box width and length from (w: " + coOrds[2] + ", h: " + coOrds[3] + ")");
                        }
                        if (widthBound > imageWidth || lengthBound > imageLength) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Failed to parse ocr bounding box width and length from (w: " + coOrds[2] + ", h: " + coOrds[3] + ")");
                    }
                }
                return false;
            }
        } else {
            List<Work> pages = work.getPartsOf("Page");
            if (pages == null || pages.isEmpty()) {
                return false;
            }
            return ocrOutOfBound(pages.get(0));
        }
    }
}
