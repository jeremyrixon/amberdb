package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("SoundFile")
public interface SoundFile extends File {
   

    @Property("surface")
    public String getSurface();    
    @Property("surface")
    public void setSurface(String surface);

    @Property("carrierCapacity")
    public String getCarrierCapacity();
    @Property("carrierCapacity")
    public void setCarrierCapacity(String carrierCapacity);    

    @Property("reelSize")
    public String getReelSize();    
    @Property("reelSize")
    public void setReelSize(String reelSize);

    @Property("channel")
    public String getChannel();    
    @Property("channel")
    public void setChannel(String channel);

    @Property("soundField")
    public String getSoundField();    
    @Property("soundField")
    public void setSoundField(String soundField);

    @Property("speed")
    public String getSpeed();    
    @Property("speed")
    public void setSpeed(String speed);

    @Property("thickness")
    public String getThickness();    
    @Property("thickness")
    public void setThickness(String thickness);

    @Property("brand")
    public String getBrand();    
    @Property("brand")
    public void setBrand(String brand);

    @Property("durationType")
    public String getDurationType();    
    @Property("durationType")
    public void setDurationType(String durationType);

    @Property("duration")
    public String getDuration();    
    @Property("duration")
    public void setDuration(String duration);

    @Property("equalisation")
    public String getEqualisation();    
    @Property("equalisation")
    public void setEqualisation(String equalisation);

    @Property("blockAlign")
    public String getBlockAlign();    
    @Property("blockAlign")
    public void setBlockAlign(String blockAlign);

    @Property("framerate")
    public String getFramerate();    
    @Property("framerate")
    public void setFramerate(String framerate);

    @Property("fileContainer")
    public String getFileContainer();    
    @Property("fileContainer")
    public void setFileContainer(String fileContainer);

    @Property("bitrate")
    public String getBitrate();    
    @Property("bitrate")
    public void setBitrate(String bitrate);

    @Property("codec")
    public String getCodec();    
    @Property("codec")
    public void setCodec(String codec);

    @Property("samplingRate")
    public String getSamplingRate();    
    @Property("samplingRate")
    public void setSamplingRate(String samplingRate);
}
