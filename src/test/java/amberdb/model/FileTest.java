package amberdb.model;

import amber.checksum.Checksum;
import amber.checksum.ChecksumAlgorithm;
import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.enums.MaterialType;
import doss.core.Writables;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class FileTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void shouldReturn0InAbsenceOfFileSize() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        Assert.assertEquals(0L, file.getFileSize());
        Assert.assertEquals(0L, file.getSize());
    }

    @Test
    public void shouldSetFileSizeOnSetBlobId() throws IOException, NoSuchAlgorithmException {
        Work work = amberSession.addWork();

        Copy copy = work.addCopy();
        File file = copy.addFile();
        file.put(Writables.wrap("TEXT"));
        Assert.assertEquals(4L, file.getFileSize());
        Assert.assertEquals(4L, file.getSize());

        // replace file
        file.put(Writables.wrap("TEXT5"));
        Assert.assertEquals(5L, file.getFileSize());
        Assert.assertEquals(5L, file.getSize());

        // point to non-existent blob
        file.setBlobIdAndSize(0L);
        Assert.assertEquals(0L, file.getFileSize());
        Assert.assertEquals(0L, file.getSize());

        copy = work.addCopy();
        file = copy.addFile();
        file.putLegacyDoss(Paths.get("src/test/resources/hello.txt"));
        Assert.assertEquals(5L, file.getFileSize());
        Assert.assertEquals(5L, file.getSize());

        copy = work.addCopy();
        file = copy.addFile();
        byte[] testFile = new byte[] {1,2,3,4,5,6};
        byte[] cs = MessageDigest.getInstance("sha1").digest(testFile);
        file.putWithChecksumValidation(Writables.wrap(testFile),
                new Checksum(ChecksumAlgorithm.fromString("sha1"),
                        org.apache.commons.codec.binary.Hex.encodeHexString(cs)));
        Assert.assertEquals(6L, file.getFileSize());
        Assert.assertEquals(6L, file.getSize());


    }

    @Test
    public void shouldReturnTheChecksumCreationDate() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        
        Date date = new Date();
        file.setChecksumGenerationDate(date);
        Assert.assertEquals(date, file.getChecksumGenerationDate());
    }
    
    @Test
    public void shouldResetTechnicalProperties() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addImageFile();
        ImageFile imageFile = (ImageFile) file;
        long blobId = 1L;
        String mimeType = "image/jpg";
        String fileName = "image_file_name.jpg";
        String fileFormat = "Image";
        String fileFormatVersion = "1.0";
        long fileSize = 9000L;
        String compression = "no";
        String checksum = "1x2f90afdanjfdkaj";
        String checksumType = "SHA1";
        Date checksumDate = new Date();
        String device = "printer";
        String deviceSerialNumber = "1sx21";
        String software = "paintpaw";
        String softwareSerialNumber = "1s3s";
        String encoding = "utf8";
        String dcmCopyPid = "nla.aus-122325";
        
        String resolution = "400 x 400";
        String resolutionUnit = "dpi";
        String colourSpace = "colour space";
        String orientation = "orientation";
        int imageWidth = 2031;
        int imageLength = 3802;
        String manufacturerMake = "manufacturer make";
        String manufacturerName = "manufacturer name";
        String manufacturerSerialNumber = "manufacturer serial number";
        Date applicationDateCreated = new Date();
        String application = "application";
        Date dateDigitised = new Date();
        String samplesPerPixel = "samples per pixel";
        String bitDepth = "bit depth";
        String photometric = "photometric";
        String location = "location";
        String colourProfile = "colour profile";
        String cpLocation = "cp location";
        String zoomLevel = "zoom level";
        
        imageFile.setBlobIdAndSize(blobId);
        imageFile.setMimeType(mimeType);
        imageFile.setFileName(fileName);
        imageFile.setFileFormat(fileFormat);
        imageFile.setFileFormatVersion(fileFormatVersion);
        imageFile.setFileSize(fileSize);
        imageFile.setCompression(compression);
        imageFile.setChecksum(checksum);
        imageFile.setChecksumType(checksumType);
        imageFile.setChecksumGenerationDate(checksumDate);
        imageFile.setDevice(device);
        imageFile.setDeviceSerialNumber(deviceSerialNumber);
        imageFile.setSoftware(software);
        imageFile.setSoftwareSerialNumber(softwareSerialNumber);
        imageFile.setEncoding(encoding);
        imageFile.setDcmCopyPid(dcmCopyPid);
        imageFile.setResolution(resolution);
        imageFile.setResolutionUnit(resolutionUnit);
        imageFile.setColourSpace(colourSpace);
        imageFile.setOrientation(orientation);
        imageFile.setImageWidth(imageWidth);
        imageFile.setImageLength(imageLength);
        imageFile.setManufacturerMake(manufacturerMake);
        imageFile.setManufacturerModelName(manufacturerName);
        imageFile.setManufacturerSerialNumber(manufacturerSerialNumber);
        imageFile.setApplicationDateCreated(applicationDateCreated);
        imageFile.setApplication(application);
        imageFile.setDateDigitised(dateDigitised);
        imageFile.setSamplesPerPixel(samplesPerPixel);
        imageFile.setBitDepth(bitDepth);
        imageFile.setPhotometric(photometric);
        imageFile.setLocation(location);
        imageFile.setColourProfile(colourProfile);
        imageFile.setCpLocation(cpLocation);
        imageFile.setZoomLevel(zoomLevel);
        
        Assert.assertTrue(blobId == file.getBlobId());
        Assert.assertEquals(mimeType, file.getMimeType());
        Assert.assertEquals(fileName, file.getFileName());
        Assert.assertEquals(fileFormat, file.getFileFormat());
        Assert.assertEquals(fileFormatVersion, file.getFileFormatVersion());
        Assert.assertEquals(fileSize, file.getFileSize());
        Assert.assertEquals(compression, file.getCompression());
        Assert.assertEquals(checksum, file.getChecksum());
        Assert.assertEquals(checksumType, file.getChecksumType());
        Assert.assertEquals(checksumDate,file.getChecksumGenerationDate());
        Assert.assertEquals(device, file.getDevice());
        Assert.assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        Assert.assertEquals(software, file.getSoftware());
        Assert.assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        Assert.assertEquals(encoding, file.getEncoding());
        Assert.assertEquals(dcmCopyPid, file.getDcmCopyPid());
        Assert.assertEquals(resolution, ((ImageFile) file).getResolution());
        Assert.assertEquals(resolutionUnit, ((ImageFile) file).getResolutionUnit());
        Assert.assertEquals(colourSpace, ((ImageFile) file).getColourSpace());
        Assert.assertEquals(orientation, ((ImageFile) file).getOrientation());
        Assert.assertTrue(imageWidth == ((ImageFile) file).getImageWidth());
        Assert.assertTrue(imageLength == ((ImageFile) file).getImageLength());
        Assert.assertEquals(manufacturerMake, ((ImageFile) file).getManufacturerMake());
        Assert.assertEquals(manufacturerName, ((ImageFile) file).getManufacturerModelName());
        Assert.assertEquals(manufacturerSerialNumber, ((ImageFile) file).getManufacturerSerialNumber());
        Assert.assertEquals(applicationDateCreated, ((ImageFile) file).getApplicationDateCreated());
        Assert.assertEquals(application, ((ImageFile) file).getApplication());
        Assert.assertEquals(dateDigitised, ((ImageFile) file).getDateDigitised());
        Assert.assertEquals(samplesPerPixel, ((ImageFile) file).getSamplesPerPixel());
        Assert.assertEquals(bitDepth, ((ImageFile) file).getBitDepth());
        Assert.assertEquals(photometric, ((ImageFile) file).getPhotometric());
        Assert.assertEquals(colourProfile,((ImageFile) file).getColourProfile());
        Assert.assertEquals(cpLocation,((ImageFile) file).getCpLocation());
        Assert.assertEquals(location, ((ImageFile) file).getLocation());
        Assert.assertEquals(zoomLevel, ((ImageFile) file).getZoomLevel());
        Assert.assertTrue(file instanceof ImageFile);
        
        file = file.resetTechnicalProperties(MaterialType.TEXT);
        Assert.assertTrue(file instanceof File);
        Assert.assertTrue(blobId == file.getBlobId());
        Assert.assertEquals(mimeType, file.getMimeType());
        Assert.assertEquals(fileName, file.getFileName());
        Assert.assertEquals(fileFormat, file.getFileFormat());
        Assert.assertEquals(fileFormatVersion, file.getFileFormatVersion());
        Assert.assertEquals(fileSize, file.getFileSize());
        Assert.assertEquals(compression, file.getCompression());
        Assert.assertEquals(checksum, file.getChecksum());
        Assert.assertEquals(checksumType, file.getChecksumType());
        Assert.assertEquals(checksumDate,file.getChecksumGenerationDate());
        Assert.assertEquals(device, file.getDevice());
        Assert.assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        Assert.assertEquals(software, file.getSoftware());
        Assert.assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        Assert.assertEquals(encoding, file.getEncoding());
        Assert.assertEquals(dcmCopyPid, file.getDcmCopyPid());
        
        file = file.resetTechnicalProperties(MaterialType.IMAGE);
        imageFile = (ImageFile) file;
        Assert.assertNull(imageFile.getResolution());
        Assert.assertNull(imageFile.getResolutionUnit());
        Assert.assertNull(imageFile.getColourSpace());
        Assert.assertNull(imageFile.getOrientation());
        Assert.assertNull(imageFile.getImageWidth());
        Assert.assertNull(imageFile.getImageLength());
        Assert.assertNull(imageFile.getManufacturerMake());
        Assert.assertNull(imageFile.getManufacturerModelName());
        Assert.assertNull(imageFile.getManufacturerSerialNumber());
        Assert.assertNull(imageFile.getApplicationDateCreated());
        Assert.assertNull(imageFile.getApplication());
        Assert.assertNull(imageFile.getDateDigitised());
        Assert.assertNull(imageFile.getSamplesPerPixel());
        Assert.assertNull(imageFile.getBitDepth());
        Assert.assertNull(imageFile.getPhotometric());
        Assert.assertNull(imageFile.getColourProfile());
        Assert.assertNull(imageFile.getCpLocation());
        Assert.assertNull(imageFile.getLocation());
        Assert.assertNull(imageFile.getZoomLevel());
    }
}
