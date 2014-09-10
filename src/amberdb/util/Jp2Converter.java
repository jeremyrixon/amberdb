package amberdb.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/*
 * This class is to convert an image (only tiff for now) to jpeg 2000 (.jp2),
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
    private static final String JP2_CONVERTER_PATH = "/usr/local/bin/kdu_compress";
    private static final String IMG_CONVERTER_PATH = "/usr/bin/convert";

    Path imgConverter;
    Path jp2Converter;
    Tika tika;

    public Jp2Converter(Path jp2Converter, Path imgConverter) {
        this.jp2Converter = jp2Converter;
        this.imgConverter = imgConverter;
        this.tika = new Tika();
    }

    public void convert(Path srcPath) throws Exception {
        if (!Files.exists(srcPath)) {
            throw new Exception("source (" + srcPath.toString() + ") does not exist");
        } else if (Files.isDirectory(srcPath)) {
            convert(srcPath, srcPath);
        } else {
            convertFileToDir(srcPath, srcPath.getParent());
        }
    }

    public void convert(Path srcPath, Path dstPath) throws Exception {
        if (!Files.exists(srcPath)) {
            throw new Exception("source (" + srcPath.toString() + ") does not exist");
        }

        if (Files.isDirectory(srcPath)) {
            // A directory
            if (!Files.exists(dstPath)) {
                // Create it
                Files.createDirectories(dstPath);
            }
            if (Files.isDirectory(dstPath)) {
                // process all files (tiff only for now) in srcPath
                final List<Path> files = new ArrayList<Path>();
                Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (file.toString().endsWith(".tif") || file.toString().endsWith(".tiff")) {
                            files.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                for (Path file : files) {
                    try {
                        convertFileToDir(file, dstPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new Exception("source (" + srcPath.toString() + ") is a directory but destination (" + dstPath + ") is not.");
            }
        } else if (Files.isRegularFile(srcPath)) {
            // A file
            if (Files.exists(dstPath) && Files.isDirectory(dstPath)) {
                convertFileToDir(srcPath, dstPath);
            } else {
                convertFile(srcPath, dstPath);
            }
        } else {
            throw new Exception("Can't convert file " + srcPath.toString());
        }
    }

    public void convertFileToDir(Path srcFilePath, Path dstDirPath)  throws Exception{
        String srcFilename = srcFilePath.getFileName().toString();
        int pos = srcFilename.lastIndexOf('.');
        String dstFilename = ((pos > 0) ? srcFilename.substring(0, pos) : srcFilename) + ".jp2";
        Path dstFilePath = dstDirPath.resolve(dstFilename);
        convertFile(srcFilePath, dstFilePath);
    }

    public void convertFile(Path srcFilePath, Path dstFilePath) throws Exception {
        log("***Convert " + srcFilePath.toString() + " to " + dstFilePath.toString());
        // Jp2 file must end with .jp2
        if (!dstFilePath.getFileName().toString().endsWith(".jp2")) {
            throw new Exception("Jpeg2000 file (" + dstFilePath.toString() + ") must end with .jp2");
        }

        // For now, only convert tiff to jp2
        String contentType = new Tika().detect(srcFilePath.toFile());
        if (!"image/tiff".equals(contentType)) {
            throw new Exception("Not a tiff file");
        }

        long startTime = System.currentTimeMillis();

        // Get image info - use JAI to read only the tiff header
        ImageInfo imgInfo = new ImageInfo(srcFilePath);

        Path tmpFilePath = dstFilePath.getParent().resolve("tmp_" + dstFilePath.getFileName() + ".tif");

        try {
            if (imgInfo.samplesPerPixel == 1 && imgInfo.bitsPerSample == 1) {
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
                // Uncompress image as kakadu can't process compressed tiff
                convertUncompress(srcFilePath, tmpFilePath);
            }

            Path fileToCreateJp2 = Files.exists(tmpFilePath) ? tmpFilePath : srcFilePath;

            // Create jp2
            try {
                // Create jp2 with kakadu kdu_compress
                createJp2(fileToCreateJp2, dstFilePath);
            } catch (Exception e1) {
                // It'd be good to send an email to someone so we know what's wrong!
                log("Kakadu kdu_compress error: " + fileToCreateJp2.toString() + "\n" + e1.toString());
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
        log("+++Done. Time taken: " + (endTime - startTime) + " milliseconds");
    }

    // Convert the bit depth of an image
    private void convertBitdepth(Path srcFilePath, Path dstFilePath, int bitDepth) throws Exception {
        convertImage(srcFilePath, dstFilePath, "-depth", "" + bitDepth);
    }

    // Convert an image to true colour
    private void convertTrueColour(Path srcFilePath, Path dstFilePath) throws Exception {
        convertImage(srcFilePath, dstFilePath, "-type", "TrueColor");
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
        // Print command
        log("Command: " + StringUtils.join(cmd, ' '));

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
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
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

    private static void log(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) throws Exception {
        Jp2Converter j2c = new Jp2Converter(Paths.get(JP2_CONVERTER_PATH), Paths.get(IMG_CONVERTER_PATH));
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java jp2.Jp2Converter <source directory/file> [<destination directory/file>]");
            System.exit(1);
        }
        if (args.length == 2) {
            j2c.convert(Paths.get(args[0]), Paths.get(args[1]));
        } else {
            j2c.convert(Paths.get(args[0]));
        }
    }

    class ImageInfo {
        int compression, samplesPerPixel, bitsPerSample, photometric;

        public ImageInfo(Path filePath) throws Exception {
            // Read image metadata using metadata-extractor
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
        }

        private int getTagValue(ExifIFD0Directory directory, int tagNo) {
            return directory.getIntArray(tagNo)[0];
        }
    }

}
