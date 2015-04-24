package amberdb.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.AmberSession;
import amberdb.relation.IsFileOf;
import amberdb.relation.DescriptionOf;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import doss.Blob;
import doss.BlobStore;
import doss.BlobTx;
import doss.NoSuchBlobException;
import doss.Writable;
import doss.core.Writables;
import doss.local.LocalBlobStore;

@TypeValue("File")
public interface File extends Node {

    @Property("blobId")
    public void setBlobId(Long id);

    @Property("blobId")
    public Long getBlobId();

    @Property("mimeType")
    public void setMimeType(String mimeType);

    @Property("mimeType")
    public String getMimeType();
    
    /**
     * Name of the original file upload
     */
    @Property("fileName")
    public void setFileName(String fileName);

    @Property("fileName")
    public String getFileName();
    
    @Property("fileFormat")
    public void setFileFormat(String fileFormat);

    @Property("fileFormat")
    public String getFileFormat();
    
    @Property("fileFormatVersion")
    public void setFileFormatVersion(String fileFormatVersion);

    @Property("fileFormatVersion")
    public String getFileFormatVersion();
    
    @Property("fileSize")
    public void setFileSize(long fileSize);

    @JavaHandler
    public long getFileSize();
    
    @Property("compression")
    public void setCompression(String compression);

    @Property("compression")
    public String getCompression();
    
    @Property("checksum")
    public void setChecksum(String checksum);

    @Property("checksum")
    public String getChecksum();
    
    @Property("checksumType")
    public void setChecksumType(String checksumType);

    @Property("checksumType")
    public String getChecksumType();
    
    @Property("device")
    public String getDevice();
    
    @Property("device")
    public void setDevice(String device);
    
    @Property("deviceSerialNumber")
    public String getDeviceSerialNumber();

    @Property("deviceSerialNumber")
    public void setDeviceSerialNumber(String deviceSerialNumber);
    
    @Property("software")
    public String getSoftware();
    
    @Property("software")
    public void setSoftware(String software);
    
    @Property("softwareSerialNumber")
    public String getSoftwareSerialNumber();
    
    @Property("softwareSerialNumber")
    public void setSoftwareSerialNumber(String softwareSerialNumber);
    
    @Property("encoding")
    public String getEncoding();
    
    @Property("encoding")
    public void setEncoding(String encoding);
    
    /**
     * This property is encoded as a JSON Array - You probably want to use getToolId to get this property
     */
    @Property("toolId")
    public String getJSONToolId();

    /**
     * This property is encoded as a JSON Array - You probably want to use setToolId to set this property
     */
    @Property("toolId")
    public void setJSONToolId(String toolId);

    /**
     * This method handles the JSON deserialisation of the toolId Property
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<Long> getToolId() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON serialisation of the toolId Property
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setToolId(List<Long> toolId) throws JsonParseException, JsonMappingException, IOException;
    
    /*
     * Fields migrated from DCM
     */
    @Property("dcmCopyPid")
    public String getDcmCopyPid();
    
    @Property("dcmCopyPid")
    public void setDcmCopyPid(String dcmCopyPid);
   
    @Adjacency(label = IsFileOf.label)
    public Copy getCopy();

    @JavaHandler
    public long getSize();

    @JavaHandler
    public SeekableByteChannel openChannel() throws IOException;

    @JavaHandler
    public InputStream openStream() throws IOException;

    @JavaHandler
    void put(Path source) throws IOException;

    @JavaHandler
    void put(Writable writable) throws IOException;

    @JavaHandler
    void putLegacyDoss(Path dossPath) throws IOException;

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    void removeDescription(final Description description);

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, File {
        static ObjectMapper mapper = new ObjectMapper();

        private BlobStore getBlobStore() {
            return AmberSession.ownerOf(g()).getBlobStore();
        }

        private Blob getBlob() throws NoSuchBlobException, IOException {
            if (getBlobId() == null) {
                throw new NoSuchBlobException();
            }
            return getBlobStore().get(getBlobId());
        }
        
        @Override
        public long getFileSize() {
            Long fileSize = this.asVertex().getProperty("fileSize");
            return (fileSize == null)? 0L : fileSize;  
        }
        
        /**
         * Return the size of the blob. If an exception occurs try and 
         * return the fileSize property. If that fails, return 0L.
         */
        @Override
        public long getSize() {
            try {
                return getBlob().size();
            } catch (NoSuchBlobException | IOException e) {
                // As a last resort see if the file has a size property to return
                return getFileSize();
            }
        }

        @Override
        public SeekableByteChannel openChannel() throws IOException {
            return getBlob().openChannel();
        }

        @Override
        public InputStream openStream() throws IOException {
            return getBlob().openStream();
        }

        @Override
        public void put(Path source) throws IOException {
            put(Writables.wrap(source));
        }

        @Override
        public void put(Writable writable) throws IOException {
            try (BlobTx tx = getBlobStore().begin()) {
                Blob blob = tx.put(writable);
                setBlobId(blob.id());
                tx.commit();
            }
        }

        @Override
        public void putLegacyDoss(Path dossPath) throws IOException {
            try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) ((LocalBlobStore) getBlobStore()).begin()) {
                Long blobId = tx.putLegacy(dossPath);
                setBlobId(blobId);
                tx.commit();
            }
        }

        @Override
        public List<Long> getToolId() throws JsonParseException, JsonMappingException, IOException {
            String toolId = getJSONToolId();
            if (toolId == null || toolId.isEmpty())
                return new ArrayList<Long>();
            return mapper.readValue(toolId, new TypeReference<List<Long>>() {});
        }

        @Override
        public void setToolId(List<Long> toolId) throws JsonParseException, JsonMappingException, IOException {
            setJSONToolId(mapper.writeValueAsString(toolId));
        }
    }
}
