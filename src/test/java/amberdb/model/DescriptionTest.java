package amberdb.model;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.enums.CopyRole;
import com.google.common.collect.Sets;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class DescriptionTest extends AbstractDatabaseIntegrationTest {
    private String objId;
    
    @Before
    public void startup() {
        amberSession = amberDb.begin();
        Work work = amberSession.addWork();
        objId = work.getObjId();
        Copy masterCopy = work.addCopy();
        masterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        
        GeoCoding gc = work.addGeoCoding();
        IPTC iptc = work.addIPTC();
        CameraData cd = masterCopy.addCameraData();
        Assert.assertEquals("GeoCoding", gc.getType());
        Assert.assertEquals("IPTC", iptc.getType());
        Assert.assertEquals("CameraData", cd.getType());
        
        gc.setLatitude("53,17.7924S");
        gc.setLongitude("194,7.8465E");
        gc.setMapDatum("WGS-84");
        gc.setTimeStamp(new Date());
        
        //for (String key : gc.asVertex().getPropertyKeys()) {
        //    System.out.println("key : " + key);
        //}
        
        iptc.setSubLocation("Parkes");
        iptc.setCity("Canberra");
        iptc.setProvince("ACT");
        iptc.setCountry("Australia");
        iptc.setISOCountryCode("AUS");
        iptc.setWorldRegion("South Asia");
        iptc.setDigitalSourceType("Original digital capture of a real life scene");
        iptc.setEvent("XXXIX Olympic Summer Games (Beijing)");
        
        cd.setExposureTime("1/100 at f/2.8");
        cd.setExposureFNumber("1/1000 at f/2.8");
        cd.setExposureMode("Auto");
        cd.setExposureProgram("Auto");
        cd.setIsoSpeedRating("1600");
        cd.setFocalLength("24.0mm");
        cd.setLens("EF24mm f/1.4L USM");
        cd.setMeteringMode("Evaluative");
        cd.setWhiteBalance("Auto");
        cd.setFileSource("Digital Still Camera (DSC)");
        
        amberSession.commit();
    }
    
    @After
    public void teardown() throws IOException {
        if (amberSession != null) {
            amberSession.close();
        }
    }
    
    @Test
    public void testGetDescriptions() {
        Work work = amberSession.findWork(objId);
        Iterable<Description> workDescriptions = work.getDescriptions();
        Assert.assertNotNull(workDescriptions);
        Assert.assertEquals(IteratorUtils.toList(workDescriptions.iterator()).size(), 2);
        Copy masterCopy = work.getCopy(CopyRole.MASTER_COPY);
        Iterable<Description> copyDescriptions = masterCopy.getDescriptions();
        Assert.assertNotNull(copyDescriptions);
        Assert.assertEquals(IteratorUtils.toList(copyDescriptions.iterator()).size(), 1);
    }

    @Test
    public void testGetADescription() {
        long sessId = amberSession.suspend();
        amberSession.recover(sessId);
        Work work = amberSession.findWork(objId);
        Description workGeocoding = work.getDescription("GeoCoding");
        Assert.assertNotNull(workGeocoding);
        Assert.assertEquals(workGeocoding.getType(), "GeoCoding");
    }
    
    @Test
    public void testGetGeocoding() {
        Work work = amberSession.findWork(objId);
        GeoCoding workGeocoding = work.getGeoCoding();
        Assert.assertNotNull(workGeocoding);
        Assert.assertEquals(workGeocoding.getLatitude(), "53,17.7924S");
        Assert.assertEquals(workGeocoding.getLongitude(), "194,7.8465E");
        Assert.assertEquals(workGeocoding.getMapDatum(), "WGS-84");
    }
    
    @Test
    public void testGetIPTC() {
        Work work = amberSession.findWork(objId);
        IPTC workIPTC = work.getIPTC();
        Assert.assertNotNull(workIPTC);
        Assert.assertEquals(workIPTC.getSubLocation(), "Parkes");
        Assert.assertEquals(workIPTC.getCity(), "Canberra");
        Assert.assertEquals(workIPTC.getProvince(), "ACT");
        Assert.assertEquals(workIPTC.getCountry(), "Australia");
    }
    
    @Test
    public void testGetCameraData() {
        Work work = amberSession.findWork(objId);
        Copy masterCopy = work.getCopy(CopyRole.MASTER_COPY);
        CameraData copyCameraData = masterCopy.getCameraData();
        Assert.assertNotNull(copyCameraData);
        Assert.assertEquals(copyCameraData.getExposureTime(), "1/100 at f/2.8");
        Assert.assertEquals(copyCameraData.getExposureMode(), "Auto");
    }

    @Test
    public void propertySetHasAllProperties() {
        Work work = amberSession.addWork();
        Copy masterCopy = work.addCopy();
        CameraData cd = masterCopy.addCameraData();
        cd.setExposureProgram("ex");
        work.getDescriptions();
        Assert.assertEquals(cd.getPropertyKeySet(), Sets.newHashSet("exposureProgram", "type"));
    }
}
