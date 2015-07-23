package amberdb.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationUtils {
	
	private static final Logger log = LoggerFactory.getLogger(DurationUtils.class);
	
	/**
     * Converts the duration in the format of HH:MM:SS:ss to HH:MM:SS
     * @param periodHHMMSSmm
     * @return
     */
    public static String convertDuration(final String periodHHMMSSmm){
    	String newDuration = periodHHMMSSmm;
    	PeriodFormatter hoursMinutesSecondsMilli = new PeriodFormatterBuilder()
    	    .appendHours()
    	    .appendSeparator(":")
    	    .appendMinutes()
    	    .appendSeparator(":")
    	    .appendSeconds()
    	    .appendSeparator(":")
    	    .appendMillis()
    	    .toFormatter();
        try{
        	if (StringUtils.isNotBlank(periodHHMMSSmm)){
        		Period period = hoursMinutesSecondsMilli.parsePeriod(periodHHMMSSmm);
        		newDuration = String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
        	}
        }catch(IllegalArgumentException e){
            log.error("Invalid duration format: " + periodHHMMSSmm);
        }
        return newDuration;
    }
}
