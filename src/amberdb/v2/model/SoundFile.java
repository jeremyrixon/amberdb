package amberdb.v2.model;


import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class SoundFile extends File {

    @Column
    private String fileName;
    @Column
    private String software;
    @Column
    private String thickness;
    @Column
    private String channel;
    @Column
    private String bitrate;
    @Column
    private String mimeType;
    @Column
    private String durationType;
    @Column
    private String speed;
    @Column
    private String duration;
    @Column
    private String toolId;
    @Column
    private String checksum;
    @Column
    private String soundField;
    @Column
    private String fileContainer;
    @Column
    private String brand;
    @Column
    private String surface;
    @Column
    private String equalisation;
    @Column
    private String encoding;
    @Column
    private String codec;
    @Column
    private int fileSize;
    @Column
    private String reelSize;
    @Column
    private String carrierCapacity;
    @Column
    private String bitDepth;
    @Column
    private String checksumType;
    @Column
    private String samplingRate;
    @Column
    private String compression;
    @Column
    private String fileFormat;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getSoundField() {
        return soundField;
    }

    public void setSoundField(String soundField) {
        this.soundField = soundField;
    }

    public String getFileContainer() {
        return fileContainer;
    }

    public void setFileContainer(String fileContainer) {
        this.fileContainer = fileContainer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getEqualisation() {
        return equalisation;
    }

    public void setEqualisation(String equalisation) {
        this.equalisation = equalisation;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getReelSize() {
        return reelSize;
    }

    public void setReelSize(String reelSize) {
        this.reelSize = reelSize;
    }

    public String getCarrierCapacity() {
        return carrierCapacity;
    }

    public void setCarrierCapacity(String carrierCapacity) {
        this.carrierCapacity = carrierCapacity;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    public String getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(String samplingRate) {
        this.samplingRate = samplingRate;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
