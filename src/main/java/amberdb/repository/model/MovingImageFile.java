package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import amberdb.util.DurationUtils;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class MovingImageFile extends File {
    private String surface;
    private String carrierCapacity;
    private String reelSize;
    private String channel;
    private String soundField;
    private String speed;
    private String thickness;
    private String brand;
    private String durationType;
    private String duration;
    private String equalisation;
    private Integer blockAlign;
    private Integer framerate;
    private String fileContainer;
    private String bitDepth;
    private String bitrate;
    private String codec;
    private String samplingRate;

    public Float getDurationAsSeconds() {
        return DurationUtils.convertDurationToSeconds(getDuration());
    }

    public void setDurationAsSeconds(Float durationAsSeconds) {
        setDuration(DurationUtils.convertDurationFromSeconds(durationAsSeconds));
    }

    public String getDurationAsHHMMSS(){
        return DurationUtils.convertDuration(getDuration());
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getCarrierCapacity() {
        return carrierCapacity;
    }

    public void setCarrierCapacity(String carrierCapacity) {
        this.carrierCapacity = carrierCapacity;
    }

    public String getReelSize() {
        return reelSize;
    }

    public void setReelSize(String reelSize) {
        this.reelSize = reelSize;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSoundField() {
        return soundField;
    }

    public void setSoundField(String soundField) {
        this.soundField = soundField;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEqualisation() {
        return equalisation;
    }

    public void setEqualisation(String equalisation) {
        this.equalisation = equalisation;
    }

    public Integer getBlockAlign() {
        return blockAlign;
    }

    public void setBlockAlign(Integer blockAlign) {
        this.blockAlign = blockAlign;
    }

    public Integer getFramerate() {
        return framerate;
    }

    public void setFramerate(Integer framerate) {
        this.framerate = framerate;
    }

    public String getFileContainer() {
        return fileContainer;
    }

    public void setFileContainer(String fileContainer) {
        this.fileContainer = fileContainer;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(String samplingRate) {
        this.samplingRate = samplingRate;
    }
}
