package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import amberdb.relation.IsCopyOf;
import amberdb.relation.IsSourceCopyOf;
import amberdb.relation.IsFileOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import doss.Writable;

/**
 * A physical or digital manifestation of a {@link Work}. The library may hold
 * one or more copies of a work, e.g. the original paper version of a piece of
 * sheet music, a microform replica and a set of digital replicas made from
 * either the original or the microform copy.
 * 
 * Copies may be either original or derived manually or automatically from
 * another copy. For the purposes of the digital library if the source of a
 * derivative is unknown we consider it original.
 */
@TypeValue("Copy")
public interface Copy extends Node {
    @Property("copyRole")
    public String getCopyRole();

    @Property("copyRole")
    public void setCopyRole(String copyRole);

    @Property("carrier")
    public String getCarrier();

    @Property("carrier")
    public void setCarrier(String carrier);

    /**
     * The source copy which this copy was derived from. Null if this copy is
     * original or the source copy is unknown.
     */
    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.OUT)
    public Copy getSourceCopy();

    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.OUT)
    public void setSourceCopy(Copy sourceCopy);

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public Iterable<File> getFiles();

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File getFile();

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File addFile();

    @JavaHandler
    File addFile(Path source, String mimeType) throws IOException;

    @JavaHandler
    File addFile(Writable contents, String mimeType) throws IOException;

    @Adjacency(label = IsCopyOf.label)
    public Work getWork();

    abstract class Impl implements JavaHandlerContext<Vertex>, Copy {

        @Override
        public File addFile(Path source, String mimeType) throws IOException {
            File file = addFile();
            file.put(source);
            file.setMimeType(mimeType);
            return file;
        }

        @Override
        public File addFile(Writable contents, String mimeType) throws IOException {
            File file = addFile();
            file.put(contents);
            file.setMimeType(mimeType);
            return file;
        }
    }

}
