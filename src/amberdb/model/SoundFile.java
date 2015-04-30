package amberdb.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
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
    public Integer getBlockAlign();    
    @Property("blockAlign")
    public void setBlockAlign(Integer blockAlign);

    @Property("framerate")
    public Integer getFramerate();    
    @Property("framerate")
    public void setFramerate(Integer framerate);

    @Property("fileContainer")
    public String getFileContainer();    
    @Property("fileContainer")
    public void setFileContainer(String fileContainer);

    @Property("bitDepth")
    public String getBitDepth();
    @Property("bitDepth")
    public void setBitDepth(String bitDepth);

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

    @JavaHandler
    public Float getDurationAsSeconds() throws Exception;

    @JavaHandler
    public void setDurationAsSeconds(Float durationAsSeconds) throws Exception;

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, SoundFile {
        private Pattern durationPattern = Pattern.compile("(\\d\\d+):(\\d\\d):(\\d\\d)(:\\d\\d?)?");

        @Override
        public Float getDurationAsSeconds() throws Exception {
            String duration = getDuration();
            if (duration != null && !duration.isEmpty()) {
                Matcher matcher = durationPattern.matcher(duration);
                int hour = 0, minute = 0, second = 0;
                float fraction = 0;
                if (matcher.matches()) {
                    hour = Integer.parseInt(matcher.group(1), 10);
                    minute = Integer.parseInt(matcher.group(2), 10);
                    second = Integer.parseInt(matcher.group(3), 10);
                    if (matcher.group(4) != null) {
                        fraction = Float.parseFloat("." + matcher.group(4).substring(1));
                    }
                    if (minute < 60 && second < 60) {
                        return hour * 3600 + minute * 60 + second + fraction;
                    } else {
                        throw new Exception("Invalid duration: " + duration);
                    }
                } else {
                    throw new Exception("Invalid duration: " + duration);
                }
            }
            return null;
        }

        @Override
        public void setDurationAsSeconds(Float durationAsSeconds) throws Exception {
            if (durationAsSeconds != null && durationAsSeconds >= 0) {
                float das = durationAsSeconds.floatValue();
                int secs = durationAsSeconds.intValue();
                String fractionStr = ((float)secs != das) ? ("" + das).substring(("" + das).indexOf('.') + 1) : null;
                int hour = secs / 3600;
                secs -= hour * 3600;
                int minute = secs / 60;
                int second = secs - minute * 60;
                String duration = StringUtils.join(new String[] {StringUtils.leftPad("" + hour, 2, "0"),
                                                                 StringUtils.leftPad("" + minute, 2, "0"),
                                                                 StringUtils.leftPad("" + second, 2, "0")}, ":") +
                                  ((fractionStr != null && !fractionStr.isEmpty()) ? ":" + fractionStr : "");
                setDuration(duration);
            } else if (durationAsSeconds < 0) {
                throw new Exception("Invalid duration: " + durationAsSeconds);
            }
        }
    }
}
