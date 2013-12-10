package amberdb.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

import amberdb.AmberSession;
import amberdb.relation.IsFileOf;

import com.tinkerpop.blueprints.Vertex;
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

    @Property("fileSize")
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

    abstract class Impl implements JavaHandlerContext<Vertex>, File {

        private BlobStore getBlobStore() {
            return AmberSession.ownerOf(g()).getBlobStore();
        }

        private Blob getBlob() throws IOException, NoSuchBlobException {
            // TODO: find a better solution for this
            if (getBlobId() == null) return null;
            return getBlobStore().get(getBlobId());
        }

        @Override
        public long getSize() {
            // TODO: find a better solution for this
            try {
                if (getBlob() == null) return 0L;
                return getBlob().size();
            } catch (Exception e) {
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
    }
}
