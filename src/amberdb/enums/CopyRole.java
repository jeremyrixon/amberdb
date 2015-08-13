package amberdb.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import amberdb.model.Copy;

/** 
 * CopyRole ENUM that provides a code and label for display.
 * 
 * the details for 0-12th copy roles must not be changed.
 * the 13 - 47 copy roles are in alphabetical order, this 
 * must be maintained at all time when a new copy role is 
 * added.
 */
public enum CopyRole {
    ORIGINAL_COPY("o", "Original",0),
    MASTER_COPY("m", "Master",1),
    DERIVATIVE_MASTER_COPY("dm", "Derivative master",2),
    CO_MASTER_COPY("c", "Co-master",3),
    DIGITAL_DISTRIBUTION_COPY("d", "Digital distribution",4),
    RELATED_METADATA_COPY("rm", "Related metadata",5),
    SUMMARY_COPY("s", "Other Summary", "No",6),
    TRANSCRIPT_COPY("tr", "Other Transcript", "No",7),
    LISTENING_1_COPY("l1", "Listening 1",8),
    LISTENING_2_COPY("l2", "Listening 2",9),
    LISTENING_3_COPY("l3", "Listening 3",10),
    WORKING_COPY("w", "Working",11),
    ANALOGUE_DISTRIBUTION_COPY("ad", "Analogue distribution",12),
    ACCESS_COPY("ac", "Access",13),
    ARCHIVE_COPY("a", "Archive",14),
    EDITED_COPY("ed", "Edited",15),
    ELECTRONIC_SUMMARY("se", "Electronic Summary", "No",16),
    ELECTRONIC_TRANSCRIPT("te", "Electronic transcript", "No",17),
    EXAMINATION_COPY("e", "Examination",18),
    FILTERED_COPY("fc", "Filtered",19),
    FINDING_AID_COPY("fa", "Finding aid",20),
    FINDING_AID_PRINT_COPY("fap", "Finding aid print",21),
    FINDING_AID_VIEW_COPY("fav", "Finding aid view",22),
    IMAGE_PACKAGE("ip", "Image Package",23),
    LIST_COPY("dl", "List",24),
    MICROFORM_COPY("mf", "Microform",25),
    OCR_METS_COPY("mt", "OCR mets",26),
    OCR_ALTO_COPY("at", "OCR alto",27),
    OCR_JSON_COPY("oc", "OCR json",28),
    PAPER_SUMMARY("sp", "Paper Summary", "No",29),
    PAPER_TRANSCRIPT("tp", "Paper transcript", "No",30),
    PRINT_COPY("p", "Print",31),
    PRODUCTION_MATERIAL("pm", "Production Material",32),
    QUICKTIME_FILE_1_COPY("sb1", "QuickTime file 1",33),
    QUICKTIME_FILE_2_COPY("sb2", "QuickTime file 2",34),
    QUICKTIME_REF_1_COPY("rb1", "QuickTime reference 1",35),
    QUICKTIME_REF_2_COPY("rb2", "QuickTime reference 2",36),
    QUICKTIME_REF_3_COPY("rb3", "QuickTime reference 3",37),
    QUICKTIME_REF_4_COPY("rb4", "QuickTime reference 4",38),
    REAL_MEDIA_FILE_COPY("sa1", "RealMedia file",39),
    REAL_MEDIA_REF_COPY("ra1", "RealMedia reference",40),
    RTF_TRANSCRIPT("tt", "rtf transcript", "No",41),
    SPECIAL_DELIVERY_COPY("sd", "Special delivery",42),
    STRUCTURAL_MAP_COPY("sm", "Structural map",43),
    THUMBNAIL_COPY("t", "Thumbnail",44),
    TIME_CODED_SUMMARY("sc", "Time coded Summary", "Yes",45),
    TIME_CODED_TRANSCRIPT_COPY("tc", "Time coded transcript", "Yes",46),
    VIEW_COPY("v", "View",47);
	

    private String code;
    private String display;
    private String timed;
    private Integer order;
    
    private CopyRole(String code, String display, Integer order) {
        this(code, display, null, order);
    }
    
    private CopyRole(String code, String display, String timed, Integer order) {
        this.code = code;
        this.display = display;
        this.timed = timed;
        this.order = order;
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

    public String timed() {
        return this.timed;
    }
    
    public int ord() {
        return this.order;
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
    
    public static Collection<Copy> reorderCopyList(Iterable<Copy> copies) {
        TreeMap<Integer, Copy> rearranged = new TreeMap<>();
        for (Copy copy : copies) {
            rearranged.put(CopyRole.fromString(copy.getCopyRole()).ord(), copy);
        }
        return rearranged.values();
    }
}
