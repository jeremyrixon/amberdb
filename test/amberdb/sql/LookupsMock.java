package amberdb.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amberdb.lookup.ToolsLu;

public abstract class LookupsMock extends Lookups {
    private Map<Long, ToolsLu> toolsHash = new HashMap<>();
    private List<ToolsLu> tools = null;
    
    @Override
    public List<ToolsLu> findTools(String deleted) {
        if (deleted.equals("N")) {
            if (tools != null) return tools;
            
            tools = new ArrayList<>();
            ToolsLu umax = new ToolsLu(1L, "UMAX PowerLook 2100XL", "", 
                    " R04/19825 and N04/83 of file NLA/15811 relate.",
                    "Used MagicScan software for image capture. NLA  asset 26105 written off and disposed of in 2003/4 FY due to breakdown. TRIM folios R04/14022",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu scanView = new ToolsLu(2L, "Scanview ScanMate F6", "", 
                    "",
                    "",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu agfaArcus = new ToolsLu(3L, "AGFA Arcus II", "", 
                    "",
                    "",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu agfaDuoScan = new ToolsLu(4L, "AGFA DuoScan T2000XL + FotoLook", "", 
                    "",
                    "",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu nikonSuper = new ToolsLu(5L, "Nikon Super CoolScan 4000ED", "", 
                    "",
                    "",
                    "Image",
                    "Camera",
                    "Device",
                    deleted);
            
            ToolsLu phaseOne = new ToolsLu(6L, "PhaseOne Phase FX", "", 
                    "",
                    "",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu sinar = new ToolsLu(7L, "Sinar Macroscan", "", 
                    "",
                    "",
                    "Image",
                    "Transmission scanner",
                    "Device",
                    deleted);
            
            ToolsLu nikonD1 = new ToolsLu(8L, "Nikon D1", "", 
                    "",
                    "",
                    "Image",
                    "Camera",
                    "Device",
                    deleted);
            
            ToolsLu canon20D = new ToolsLu(9L, "Canon 20D", "", 
                    "",
                    "",
                    "Image",
                    "Camera",
                    "Device",
                    deleted);
            
            ToolsLu kodak = new ToolsLu(10L, "Kodak ProBack", "", 
                    "",
                    "",
                    "Image",
                    "Camera",
                    "Device",
                    deleted);
            tools.add(umax);
            tools.add(scanView);
            tools.add(agfaArcus);
            tools.add(agfaDuoScan);
            tools.add(nikonSuper);
            tools.add(phaseOne);
            tools.add(sinar);
            tools.add(nikonD1);
            tools.add(canon20D);
            tools.add(kodak);
            toolsHash.put(1L, umax);
            toolsHash.put(2L, scanView);
            toolsHash.put(3L, agfaArcus);
            toolsHash.put(4L, agfaDuoScan);
            toolsHash.put(5L, nikonSuper);
            toolsHash.put(6L, phaseOne);
            toolsHash.put(7L, sinar);
            toolsHash.put(8L, nikonD1);
            toolsHash.put(9L, canon20D);
            toolsHash.put(10L, kodak);
            return tools;
        }
        return null;
    }
    
    @Override
    public void addLookupData(Long id,
            String name,
            String lbl,
            String code,
            String attribute,
            String value,
            String deleted) {
        ToolsLu newTool;
        
        if (name.equals("tools")) {
            if (toolsHash.get(id) == null) {
                newTool = new ToolsLu(id, "", "", "", "", "", "", "", deleted);
                toolsHash.put(id, newTool);
                tools = findAllActiveTools();
                tools.add(newTool);
            } else {
                newTool = toolsHash.get(id);
            }

            if (attribute.equals("name")) {
                newTool.setName(value);
            } else if (attribute.equals("resolution")) {
                newTool.setResolution(value);
            } else if (attribute.equals("serialNumber")) {
                newTool.setSerialNumber(value);
            } else if (attribute.equals("notes")) {
                newTool.setNotes(value);
            } 
        }
    }
}
