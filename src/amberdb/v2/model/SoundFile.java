package amberdb.v2.model;


import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.SoundFileMapper;

@MapWith(SoundFileMapper.class)
public class SoundFile extends Node {

    private String fileName;
    private String software;
    private String thickness;
    private String channel;
    private String bitrate;
    private String mimeType;
    private String durationType;
    private String speed;
    private String duration;
    private String toolId;
    private String checksum;
    private String soundField;
    private String fileContainer;
    private String brand;
    private String surface;
    private String equalisation;
    private String encoding;
    private String codec;
    private int fileSize;
    private String reelSize;
    private String carrierCapacity;
    private String bitDepth;
    private int blobId;
    private String checksumType;
    private String samplingRate;
    private String compression;
    private String fileFormat;

    public SoundFile(int id, int txn_start, int txn_end, String fileName, String software, String thickness,
                     String channel, String bitrate, String mimeType, String durationType, String speed,
                     String duration, String toolId, String checksum, String soundField, String fileContainer,
                     String brand, String surface, String equalisation, String encoding, String codec, int fileSize,
                     String reelSize, String carrierCapacity, String bitDepth, int blobId, String checksumType,
                     String samplingRate, String compression, String fileFormat) {
        super(id, txn_start, txn_end);
        this.fileName = fileName;
        this.software = software;
        this.thickness = thickness;
        this.channel = channel;
        this.bitrate = bitrate;
        this.mimeType = mimeType;
        this.durationType = durationType;
        this.speed = speed;
        this.duration = duration;
        this.toolId = toolId;
        this.checksum = checksum;
        this.soundField = soundField;
        this.fileContainer = fileContainer;
        this.brand = brand;
        this.surface = surface;
        this.equalisation = equalisation;
        this.encoding = encoding;
        this.codec = codec;
        this.fileSize = fileSize;
        this.reelSize = reelSize;
        this.carrierCapacity = carrierCapacity;
        this.bitDepth = bitDepth;
        this.blobId = blobId;
        this.checksumType = checksumType;
        this.samplingRate = samplingRate;
        this.compression = compression;
        this.fileFormat = fileFormat;
    }

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

    public int getBlobId() {
        return blobId;
    }

    public void setBlobId(int blobId) {
        this.blobId = blobId;
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
