package amberdb.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** 
 * CopyRole ENUM that provides a code and label for display.
 */
public enum CopyRole {
    
    ACCESS_COPY("ac", "Access"), 
    MASTER_COPY("m", "Master"), 
    DERIVATIVE_MASTER_COPY("dm", "Derivative master"),
    CO_MASTER_COPY("c", "Co-master"), 
    ORIGINAL_COPY("o", "Original"),    
    THUMBNAIL_COPY("t", "Thumbnail"),
    VIEW_COPY("v", "View"),
    EXAMINATION_COPY("e", "Examination"),    
    ARCHIVE_COPY("a", "Archive"),    
    STRUCTURAL_MAP_COPY("sm", "Structural map"),
    FINDING_AID_COPY("fa", "Finding aid"),
    FINDING_AID_PRINT_COPY("fap", "Finding aid print"),
    FINDING_AID_VIEW_COPY("fav", "Finding aid view"),
    FINDING_AID_FILTERED_COPY("faf", "Finding aid filtered"),
    MICROFORM_COPY("mf", "Microform"),
    SPECIAL_DELIVERY_COPY("sd", "Special delivery"),
    RELATED_METADATA_COPY("rm", "Related metadata"),
    LIST_COPY("dl", "List"),
    PRINT_COPY("p", "Print"),
    OCR_JSON_COPY("oc", "OCR json"),
    OCR_ALTO_COPY("at", "OCR alto"),
    OCR_METS_COPY("mt", "OCR mets"),
    ANALOGUE_DISTRIBUTION_COPY("ad", "Analogue distribution"),
    DIGITAL_DISTRIBUTION_COPY("d", "Digital distribution"),
    REAL_MEDIA_REF_COPY("ra1", "RealMedia reference"),
    REAL_MEDIA_FILE_COPY("sa1", "RealMedia file"),
    QUICKTIME_REF_1_COPY("rb1", "QuickTime reference 1"),
    QUICKTIME_REF_2_COPY("rb2", "QuickTime reference 2"),
    QUICKTIME_REF_3_COPY("rb3", "QuickTime reference 3"),
    QUICKTIME_REF_4_COPY("rb4", "QuickTime reference 4"),
    QUICKTIME_FILE_1_COPY("sb1", "QuickTime file 1"),
    QUICKTIME_FILE_2_COPY("sb2", "QuickTime file 2"),
    LISTENING_1_COPY("l1", "Listening 1"),
    LISTENING_2_COPY("l2", "Listening 2"),
    LISTENING_3_COPY("l3", "Listening 3"),
    WORKING_COPY("w", "Working"),
    EDITED_COPY("ed", "Edited"),
    FILTERED_COPY("fc", "Filtered"),
    PAPER_SUMMARY("sp", "Paper Summary"),
    PAPER_TRANSCRIPT("tp", "Paper transcript"),
    ELECTRONIC_SUMMARY("se", "Electronic Summary"),
    ELECTRONIC_TRANSCRIPT("te", "Electronic transcript"),
    TIME_CODED_SUMMARY("sc", "Time coded Summary"),
    TIME_CODED_TRANSCRIPT_COPY("tc", "Time coded transcript"),
    SUMMARY_COPY("s", "Other Summary"),
    RTF_TRANSCRIPT("tt", "rtf transcript"),
    TRANSCRIPT_COPY("tr", "Other Transcript");

    private String code;
    private String display;
    
    
    private CopyRole(String code) {
        this.code = code;
    }
    
    private CopyRole(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public static CopyRole fromString(String code) {
        if (code != null) {
            for (CopyRole c : CopyRole.values()) {
                if (code.equalsIgnoreCase(c.code)) {
                    return c;
                }
            }
        }
        return null;
    }

    public String code() {
        return this.code;
    }
    
    public String display() {
        return this.display;
    }
    
    /**
     * Returns a List of <STRONG>codes</STRONG>
     */
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (CopyRole c : CopyRole.values()) {
            list.add(c.code());
        }
        return list;
    }
    
    /**
     * Returns a List of <STRONG>CopyRole</STRONG> objects that have been sorted
     * alphabetically by the CopyRole.value
     */
    public static List<CopyRole> listAlphabetically() {                      
        List<CopyRole> list = new ArrayList<CopyRole>();
        for (CopyRole c : CopyRole.values()) {
            list.add(c);
        }
               
        Collections.sort(list, new Comparator<CopyRole>() {
            public int compare(CopyRole r1, CopyRole r2) {
                return r1.display().compareTo(r2.display());
            }
        });
        
        return list;
    }
}
