package amberdb.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/*
 * This class is to convert an image (tiff or jpeg for now) to jpeg 2000 (.jp2),
 * and to make sure that the jp2 file can be delivered by the IIP image server.
 * For some reason, the IIP image server can't deliver, or deliver incorrectly a number of jp2 files
 * created by kakadu kdu_compress, and even imagemagick convert. They are:
 *  - Bitonal (black and white) images:
 *    + If the image has photometric = 0 (WhiteIsZero), IIP delivers an inverted image (black turns to white and white to black)
 *    + If the image has photometric = 1 (BlackIsZero), IIP delivers the white bits as grey.
 *  - Colour images with more than 3 channels (RGB). IIP mixes up the colours and delivers strange images.
 *  - Colour images with 16 or 24 bitdepth. IIP mixes up the colours and delivers strange images, or even can't deliver at all.
 *  - Colour images with a colour palette (photometric > 2). Kakadu can't create jp2 unless option -no_palette is used.
 *
 * The solution is to convert the original tiff to another tiff which has higher/lower bitdepth, reduced channels, etc.
 * and use this tiff to create jpeg2000 jp2.
 *  - For bitonal images: increase the bitdepth from 1 to 8, which will solve the inverted problem and make
 *    the image image look good in the delivery system.
 *  - For colour images with more than 3 channels (RGB): turn them in to 3 channels (RGB).
 *  - For colour images with 16 or 24 bitdepth: turn bitdepth into 8.
 *  - For colour images with a colour palette (photometric > 2): strip off the colour palette.
 * 
 * It's also noticed that some colour images (tiff) can't be converted to the standard jp2 due to kakadu file format support.
 * Kakadu recommends converting them to .jpx instead:
 *     Error in Kakadu File Format Support:
 *         Attempting to write a colour description (colr) box which uses JPX extended
 *         features to the image header of a baseline JP2 file.  You might like to upgrade
 *         the application to write files using the `jpx_target' object, rather than
 *         `jp2_target'.
 * Currently amberdb and banjo don't support jpx. Adding support for jpx will take place at a later time.
 * For the mean time, we'll just use imagemagick convert to convert it to jp2.
*/

public class Jp2Converter {
    private static final Logger log = LoggerFactory.getLogger(Jp2Converter.class);

    Path imgConverter;
    Path jp2Converter;
    Tika tika;

    public Jp2Converter(Path jp2Converter, Path imgConverter) {
        this.jp2Converter = jp2Converter;
        this.imgConverter = imgConverter;
        this.tika = new Tika();
    }

    public void convertFile(Path srcFilePath, Path dstFilePath) throws Exception {
        // Use metadata-extractor to get image info of the source image
        convertFile(srcFilePath,  new ImageInfo(srcFilePath), dstFilePath);
    }

    public void convertFile(Path srcFilePath, Path dstFilePath, Map<String, String> imgInfoMap) throws Exception {
        // Image info of the source image is passed in as a map
        convertFile(srcFilePath,  new ImageInfo(imgInfoMap), dstFilePath);
    }

    public void convertFile(Path srcFilePath, Path dstFilePath, String mimeType,
                            int compression, int samplesPerPixel, int bitsPerSample, int photometric) throws Exception {
        // Image info of the source image is passed in as a list of values
        convertFile(srcFilePath, new ImageInfo(mimeType, compression, samplesPerPixel, bitsPerSample, photometric), dstFilePath);
    }

    private void convertFile(Path srcFilePath, ImageInfo imgInfo, Path dstFilePath) throws Exception {
        // Main method to convert an image to a jp2 - imgInfo has to be accurate!
        // Jp2 file must end with .jp2
        if (!dstFilePath.getFileName().toString().endsWith(".jp2")) {
            throw new Exception("Jpeg2000 file (" + dstFilePath.toString() + ") must end with .jp2");
        }

        // For now, only convert tiff or jpeg to jp2
        if (!("image/tiff".equals(imgInfo.mimeType) || "image/jpeg".equals(imgInfo.mimeType))) {
            throw new Exception("Not a tiff or a jpeg file");
        }

        long startTime = System.currentTimeMillis();

        Path tmpFilePath = dstFilePath.getParent().resolve("tmp_" + dstFilePath.getFileName() + ".tif");

        try {
            if ("image/jpeg".equals(imgInfo.mimeType)) {
                // Jpeg - Convert to tiff
                convertUncompress(srcFilePath, tmpFilePath);
            } else if (imgInfo.samplesPerPixel == 1 && imgInfo.bitsPerSample == 1) {
                // Bitonal image - Convert to greyscale (8 bit depth)
                convertBitdepth(srcFilePath, tmpFilePath, 8);
            } else if (imgInfo.samplesPerPixel > 3) {
                // Image has more than 3 channels (RGB) - Convert to 3 channels (TrueColor)
                convertTrueColour(srcFilePath, tmpFilePath);
            } else if (imgInfo.samplesPerPixel > 1 && imgInfo.bitsPerSample > 8) {
                // Colour image with 16 or 24 bit depth - convert to 8 bit depth
                convertBitdepth(srcFilePath, tmpFilePath, 8);
            } else if (imgInfo.photometric > 2) {
                // Image has colour palette - Convert to 3 TrueColor
                convertTrueColour(srcFilePath, tmpFilePath);
            } else if (imgInfo.compression > 1) {
                // Uncompress image as the demo app kdu_compress can't process compressed tiff
                convertUncompress(srcFilePath, tmpFilePath);
            }

            Path fileToCreateJp2 = Files.exists(tmpFilePath) ? tmpFilePath : srcFilePath;

            // Create jp2
            try {
                // Create jp2 with kakadu kdu_compress
                createJp2(fileToCreateJp2, dstFilePath);
            } catch (Exception e1) {
                // It'd be good to send an email to someone so we know what's wrong!
                log.debug("Kakadu kdu_compress error: {}\n{}", fileToCreateJp2.toString(), e1.toString());
                // Can't create jp2 with kakadu kdu_compress, try using imagemagick convert
                createJp2ImageMagick(fileToCreateJp2, dstFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // Delete tmp file if exists
            Files.deleteIfExists(tmpFilePath);
        }

        long endTime = System.currentTimeMillis();
        log.debug("***Convert {} to {} took {} milliseconds", srcFilePath.toString(), dstFilePath.toString(), (endTime - startTime));
    }

    // Convert the bit depth of an image
    private void convertBitdepth(Path srcFilePath, Path dstFilePath, int bitDepth) throws Exception {
        convertImage(srcFilePath, dstFilePath, "-compress", "None", "-depth", "" + bitDepth);
    }

    // Convert an image to true colour
    private void convertTrueColour(Path srcFilePath, Path dstFilePath) throws Exception {
        convertImage(srcFilePath, dstFilePath, "-compress", "None", "-type", "TrueColor");
    }

    // Uncompress an image
    private void convertUncompress(Path srcFilePath, Path dstFilePath) throws Exception {
        convertImage(srcFilePath, dstFilePath, "-compress", "None");
    }

    // Convert an image with imagemagick
    private void convertImage(Path srcFilePath, Path dstFilePath, String... params) throws Exception {
        // Setup command
        String[] cmd = new String[params.length + 3];
        cmd[0] = imgConverter.toString();
        System.arraycopy(params, 0, cmd, 1, params.length);
        cmd[cmd.length - 2] = srcFilePath.toString();
        cmd[cmd.length - 1] = dstFilePath.toString();

        // And execute it
        executeCmd(cmd);
    }

    // Create jp2 with kakadu kdu_compress
    private void createJp2(Path srcFilePath, Path dstFilePath) throws Exception {
        // Use kakadu to create jp2
        executeCmd(new String[] {
                jp2Converter.toString(),
                "-i",
                srcFilePath.toString(),
                "-o",
                dstFilePath.toString(),
                "-rate",
                "0.5",
                "Clayers=1",
                "Clevels=7",
                "Cprecincts={256,256},{256,256},{256,256},{128,128},{128,128},{64,64},{64,64},{32,32},{16,16}",
                "Corder=RPCL",
                "ORGgen_plt=yes",
                "Cblk={32,32}",
                "-num_threads",
                "1",
                "Cuse_sop=yes"});
    }

    // Create jp2 with imagemagick convert
    private void createJp2ImageMagick(Path srcFilePath, Path dstFilePath) throws Exception {
        executeCmd(new String[] {
                imgConverter.toString(),
                srcFilePath.toString(),
                dstFilePath.toString()
        });
    }

    // Execute a command
    private void executeCmd(String[] cmd) throws Exception {
        // Log command
        log.debug("Run command: ", StringUtils.join(cmd, ' '));

        // Execute command
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process p = builder.start();
        p.waitFor();
        int exitVal = p.exitValue();
        String msg = "";
        if (exitVal > 0) {
            // Error - Read from error stream
            StringBuffer sb = new StringBuffer();
            String line;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(line);
                }
                br.close();
            } catch (Exception e) {
                // don't care
                e.printStackTrace();
            }
            msg = sb.toString().trim();
            throw new Exception(msg);
        }
    }

    class ImageInfo {
        String mimeType;
        int compression, samplesPerPixel, bitsPerSample, photometric;

        public ImageInfo(String mimeType, int compression, int samplesPerPixel, int bitsPerSample, int photometric) throws Exception {
            this.mimeType = mimeType;
            this.compression = compression;
            this.samplesPerPixel = samplesPerPixel;
            this.bitsPerSample = bitsPerSample;
            this.photometric = photometric;
        }

        public ImageInfo(Map<String, String> imgInfoMap) throws Exception {
            this.mimeType  = imgInfoMap.get("mimeType");
            this.compression = Integer.parseInt(imgInfoMap.get("compression"), 10);
            this.samplesPerPixel = Integer.parseInt(imgInfoMap.get("samplesPerPixel"), 10);
            this.bitsPerSample = Integer.parseInt(imgInfoMap.get("bitsPerSample"), 10);
            this.photometric = Integer.parseInt(imgInfoMap.get("photometric"), 10);
        }

        public ImageInfo(Path filePath) throws Exception {
            this.mimeType = tika.detect(filePath.toFile());
            if ("image/tiff".equals(mimeType)) {
                // Read image metadata using metadata-extractor - only for tiff
                Metadata metadata = ImageMetadataReader.readMetadata(filePath.toFile());
                ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
                if (directory == null) {
                    throw new Exception("Missing ExifIFD0Directory: " + filePath.toString());
                }

                // Compression (259)
                this.compression = getTagValue(directory, 259);

                // Samples per pixel (277)
                this.samplesPerPixel = getTagValue(directory, 277);

                // Bits per sample (258)
                this.bitsPerSample = getTagValue(directory, 258);

                // Photometric (262)
                this.photometric = getTagValue(directory, 262);
            } else {
                this.compression = this.samplesPerPixel = this.bitsPerSample = this.photometric = -1;
            }
        }

        private int getTagValue(ExifIFD0Directory directory, int tagNo) {
            return directory.getIntArray(tagNo)[0];
        }
    }
}
