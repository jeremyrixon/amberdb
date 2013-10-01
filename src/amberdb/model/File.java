package amberdb.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

import amberdb.AmberDb;
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

@TypeValue("File")
public interface File extends Node {

    @Property("blobId")
    public void setBlobId(Long id);

    @Property("blobId")
    public Long getBlobId();

    @Property("contentType")
    public void setContentType(String contentType);

    @Property("blobId")
    public String getContentType();

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

    abstract class Impl implements JavaHandlerContext<Vertex>, File {

        private BlobStore getBlobStore() {
            return AmberDb.ownerOf(g()).getBlobStore();
        }

        private Blob getBlob() throws IOException {
            return getBlobStore().get(getBlobId());
        }

        @Override
        public long getSize() {
            try {
                return getBlob().size();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
            try (BlobTx tx = getBlobStore().begin()) {
                Blob blob = tx.put(source);
                setBlobId(blob.id());
                tx.commit();
            }
        }

    }
}
