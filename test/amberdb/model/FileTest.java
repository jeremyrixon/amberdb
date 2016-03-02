package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberSession;


public class FileTest {
    private AmberSession amberDb;
    
    @Before
    public void startup() {
        amberDb = new AmberSession();           
    }
    
    @After
    public void teardown() throws IOException {
        if (amberDb != null) {
            amberDb.close();
        }       
    }
    
    @Test
    public void shouldReturn0InAbsenceOfFileSize() {
        Work work = amberDb.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        assertEquals(0L, file.getFileSize());
        assertEquals(0L, file.getSize());
    }
    
    @Test
    public void shouldReturnTheChecksumCreationDate() {
        Work work = amberDb.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        
        Date date = new Date();
        file.setChecksumGenerationDate(date);
        assertEquals(date, file.getChecksumGenerationDate());
    }
    
    @Test
    public void shouldResetTechnicalProperties() {
        Work work = amberDb.addWork();
        Copy copy = work.addCopy();
        ImageFile file = copy.addImageFile();
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
        
        file.setBlobId(blobId);
        file.setMimeType(mimeType);
        file.setFileName(fileName);
        file.setFileFormat(fileFormat);
        file.setFileFormatVersion(fileFormatVersion);
        file.setFileSize(fileSize);
        file.setCompression(compression);
        file.setChecksum(checksum);
        file.setChecksumType(checksumType);
        file.setChecksumGenerationDate(checksumDate);
        file.setDevice(device);
        file.setDeviceSerialNumber(deviceSerialNumber);
        file.setSoftware(software);
        file.setSoftwareSerialNumber(softwareSerialNumber);
        file.setEncoding(encoding);
        file.setDcmCopyPid(dcmCopyPid);
        file.setResolution(resolution);
        file.setResolutionUnit(resolutionUnit);
        file.setColourSpace(colourSpace);
        file.setOrientation(orientation);
        file.setImageWidth(imageWidth);
        file.setImageLength(imageLength);
        file.setManufacturerMake(manufacturerMake);
        file.setManufacturerModelName(manufacturerName);
        file.setManufacturerSerialNumber(manufacturerSerialNumber);
        file.setApplicationDateCreated(applicationDateCreated);
        file.setApplication(application);
        file.setDateDigitised(dateDigitised);
        file.setSamplesPerPixel(samplesPerPixel);
        file.setBitDepth(bitDepth);
        file.setPhotometric(photometric);
        file.setLocation(location);
        file.setColourProfile(colourProfile);
        file.setCpLocation(cpLocation);
        file.setZoomLevel(zoomLevel);
        
        assertTrue(blobId == file.getBlobId());
        assertEquals(mimeType, file.getMimeType());
        assertEquals(fileName, file.getFileName());
        assertEquals(fileFormat, file.getFileFormat());
        assertEquals(fileFormatVersion, file.getFileFormatVersion());
        assertEquals(fileSize, file.getFileSize());
        assertEquals(compression, file.getCompression());
        assertEquals(checksum, file.getChecksum());
        assertEquals(checksumType, file.getChecksumType());
        assertEquals(checksumDate,file.getChecksumGenerationDate());
        assertEquals(device, file.getDevice());
        assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        assertEquals(software, file.getSoftware());
        assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        assertEquals(encoding, file.getEncoding());
        assertEquals(dcmCopyPid, file.getDcmCopyPid());
        assertEquals(resolution, file.getResolution());
        assertEquals(resolutionUnit, file.getResolutionUnit());
        assertEquals(colourSpace, file.getColourSpace());
        assertEquals(orientation, file.getOrientation());
        assertTrue(imageWidth == file.getImageWidth());
        assertTrue(imageLength == file.getImageLength());
        assertEquals(manufacturerMake, file.getManufacturerMake());
        assertEquals(manufacturerName, file.getManufacturerModelName());
        assertEquals(manufacturerSerialNumber, file.getManufacturerSerialNumber());
        assertEquals(applicationDateCreated, file.getApplicationDateCreated());
        assertEquals(application, file.getApplication());
        assertEquals(dateDigitised, file.getDateDigitised());
        assertEquals(samplesPerPixel, file.getSamplesPerPixel());
        assertEquals(bitDepth, file.getBitDepth());
        assertEquals(photometric, file.getPhotometric());
        assertEquals(colourProfile,file.getColourProfile());
        assertEquals(cpLocation,file.getCpLocation());
        assertEquals(location, file.getLocation());
        assertEquals(zoomLevel, file.getZoomLevel()); 
        
        file.resetTechnicalProperties();

        assertTrue(blobId == file.getBlobId());
        assertEquals(mimeType, file.getMimeType());
        assertEquals(fileName, file.getFileName());
        assertEquals(fileFormat, file.getFileFormat());
        assertEquals(fileFormatVersion, file.getFileFormatVersion());
        assertEquals(fileSize, file.getFileSize());
        assertEquals(compression, file.getCompression());
        assertEquals(checksum, file.getChecksum());
        assertEquals(checksumType, file.getChecksumType());
        assertEquals(checksumDate,file.getChecksumGenerationDate());
        assertEquals(device, file.getDevice());
        assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        assertEquals(software, file.getSoftware());
        assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        assertEquals(encoding, file.getEncoding());
        assertEquals(dcmCopyPid, file.getDcmCopyPid());        
        assertNull(file.getResolution());
        assertNull(file.getResolutionUnit());
        assertNull(file.getColourSpace());
        assertNull(file.getOrientation());
        assertNull(file.getImageWidth());
        assertNull(file.getImageLength());
        assertNull(file.getManufacturerMake());
        assertNull(file.getManufacturerModelName());
        assertNull(file.getManufacturerSerialNumber());
        assertNull(file.getApplicationDateCreated());
        assertNull(file.getApplication());
        assertNull(file.getDateDigitised());
        assertNull(file.getSamplesPerPixel());
        assertNull(file.getBitDepth());
        assertNull(file.getPhotometric());
        assertNull(file.getColourProfile());
        assertNull(file.getCpLocation());
        assertNull(file.getLocation());
        assertNull(file.getZoomLevel());
    }
}
