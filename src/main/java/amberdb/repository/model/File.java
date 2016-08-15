package amberdb.repository.model;

import amber.checksum.Checksum;
import amber.checksum.InvalidChecksumException;
import amberdb.enums.MaterialType;
import amberdb.repository.mappers.AmberDbMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import doss.*;
import doss.core.Writables;
import doss.local.LocalBlobStore;
import org.apache.commons.beanutils.BeanUtils;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class File extends Node {
    @Column
    private Long blobId;
    @Column
    private String mimeType;
    @Column
    private String fileName;
    @Column
    private String fileFormat;
    @Column
    private String fileFormatVersion;
    @Column
    private Long fileSize;
    @Column
    private String compression;
    @Column
    private String checksum;
    @Column
    private String checksumType;
    @Column
    private Date checksumGenerationDate;
    @Column
    private String device;
    @Column
    private String deviceSerialNumber;
    @Column
    private String software;
    @Column
    private String softwareSerialNumber;
    @Column
    private String encoding;
    @Column(name="toolId")
    private String jsonToolId;
    @Column
    private String type;
    @Column
    private String dcmCopyPid;

    static ObjectMapper mapper = new ObjectMapper();
    static Set<String> mandatoryfldNames = new HashSet<>();

    static {
        Method[] methods = File.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                mandatoryfldNames.add(method.getName().substring(3).toUpperCase());
            }
        }
    }

    public void setBlobIdAndSize(Long id, BlobStore blobStore) {
        try {
            Long size = blobStore.get(id).size();
            setFileSize(size);

        } catch (NoSuchBlobException e) {
            setFileSize(0L);

        } catch (IOException e) {
            // clear the fileSize so it can be populated when getFileSize is called
            setFileSize(null);
        }
        setBlobId(id);
    }

    public long getFileSize(BlobStore blobStore) {
        Long fileSize = getFileSize();
        if (fileSize == null) {
            try {
                fileSize = getBlob(blobStore).size();
                setFileSize(fileSize);

            } catch (NoSuchBlobException e) {
                fileSize = 0L;
                setFileSize(fileSize);

            } catch (IOException e) {
                return 0L;
                // leave fileSize attribute null, so it can try again next time
            }
        }
        return fileSize;
    }

    public long getSize(BlobStore blobStore) {
        try {
            return getBlob(blobStore).size();

        } catch (NoSuchBlobException e) {
            return 0L; // no blob, so size is 0

        } catch (IOException e) {
            // As a last resort see if the file has a size property to return
            Long fileSize = getFileSize();
            if (fileSize != null) {
                return fileSize;
            }
            // no, so call it 0 for now
            return 0L;
        }
    }

    private Blob getBlob(BlobStore blobStore) throws NoSuchBlobException, IOException {
        if (getBlobId() == null) {
            throw new NoSuchBlobException();
        }
        return blobStore.get(getBlobId());
    }

    public SeekableByteChannel openChannel(BlobStore blobStore) throws IOException {
        return getBlob(blobStore).openChannel();
    }

    public InputStream openStream(BlobStore blobStore) throws IOException {
        return getBlob(blobStore).openStream();
    }

    public void put(Path source, BlobStore blobStore) throws IOException {
        put(Writables.wrap(source), blobStore);
    }

    public void putWithChecksumValidation(Path source, Checksum checksum, BlobStore blobStore) throws IOException {
        putWithChecksumValidation(Writables.wrap(source), checksum, blobStore);
    }

    public void put(Writable writable, BlobStore blobStore) throws IOException {
        try (BlobTx tx = blobStore.begin()) {
            Blob blob = tx.put(writable);
            setBlobIdAndSize(blob.id(), blobStore);
            tx.commit();
        }
    }

    public void putWithChecksumValidation(Writable writable, Checksum checksum, BlobStore blobStore) throws IOException {
        try (BlobTx tx = blobStore.begin()) {
            Blob blob = tx.put(writable);
            try {
                if (!blob.digest(checksum.getAlgorithm().algorithm()).equals(checksum.getValue())){
                    throw new InvalidChecksumException("Ingest failed. Digest mismatch");
                }
            } catch (NoSuchAlgorithmException e) {
                throw new InvalidChecksumException("Ingest failed. Checksum not found in the meta xml file");
            }
            setBlobIdAndSize(blob.id(), blobStore);
            tx.commit();
        }
    }

    public void putLegacyDoss(Path dossPath, BlobStore blobStore) throws IOException {
        try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) ((LocalBlobStore) blobStore).begin()) {
            Long blobId = tx.putLegacy(dossPath);
            setBlobIdAndSize(blobId, blobStore);
            tx.commit();
        }
    }

    public File resetTechnicalProperties(MaterialType materialType) throws InvocationTargetException, IllegalAccessException {
        Field[] fields = File.class.getDeclaredFields();
        for (Field f : fields) {
            if (!mandatoryfldNames.contains(f.getName())) {
                BeanUtils.setProperty(this, f.getName(), null);
            }
        }

        File file = this;
        if (materialType == MaterialType.IMAGE) {
            file.setType("ImageFile");
        } else if (materialType == MaterialType.MOVINGIMAGE) {
            file.setType("MovingImageFile");
        } else if (materialType == MaterialType.SOUND) {
            file.setType("SoundFile");
        } else {
            file.setType("File");
        }
        return file;
    }

    public List<Long> getToolId() {
        String toolId = getJSONToolId();
        if (toolId == null || toolId.isEmpty())
            return new ArrayList<Long>();
        return deserialiseJSONString(toolId, new TypeReference<List<Long>>() {});
    }

    public void setToolId(List<Long> toolId) throws JsonProcessingException {
        setJSONToolId(mapper.writeValueAsString(toolId));
    }

    public Long getBlobId() {
        return blobId;
    }

    public void setBlobId(Long blobId) {
        this.blobId = blobId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    public void setFileFormatVersion(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    public Date getChecksumGenerationDate() {
        return checksumGenerationDate;
    }

    public void setChecksumGenerationDate(Date checksumGenerationDate) {
        this.checksumGenerationDate = checksumGenerationDate;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getSoftwareSerialNumber() {
        return softwareSerialNumber;
    }

    public void setSoftwareSerialNumber(String softwareSerialNumber) {
        this.softwareSerialNumber = softwareSerialNumber;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getJSONToolId() {
        return jsonToolId;
    }

    public void setJSONToolId(String jsonToolId) {
        this.jsonToolId = jsonToolId;
    }

    public String getDcmCopyPid() {
        return dcmCopyPid;
    }

    public void setDcmCopyPid(String dcmCopyPid) {
        this.dcmCopyPid = dcmCopyPid;
    }
}
