package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.utils.OSProcessBuilder;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

import doss.local.LocalBlobStore;

public interface MasterImageCopy extends Copy {
    @JavaHandler
    Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException;
    
    abstract class Impl implements JavaHandlerContext<Vertex>, MasterImageCopy {
        @Override
        public Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException {
            File masterImage = this.getFile();
            if (masterImage == null || masterImage.getBlobId() == null) 
                throw new NoSuchCopyException(getWork().getId(), CopyRole.MASTER_COPY);
            
            // set the masterImage file to have file extension of .tif
            try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) ((LocalBlobStore) AmberSession.ownerOf(g()).getBlobStore()).begin()) {
                tx.setExtension(masterImage.getBlobId(), ".tif");    
                tx.commit();
                
                Path jp2ImgPath = generateImage(tiffUnCompressor, jp2Generator, masterImage);
                
                Work work = this.getWork();
                Copy ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "jp2");
                AccessImageFile acf = (AccessImageFile) ac.getFile();
                acf.setLocation(jp2ImgPath.toString());
                return ac;
            }
        }
        
        private Path generateImage(Path tiffUnCompressor, Path jp2Generator, File masterImage) throws IOException, InterruptedException {
            // Step 1: uncompress Tiff
            Path tiffInput = Paths.get("");
            Path tiffOutput = Paths.get("");
            ProcessBuilder tiffPb = new OSProcessBuilder("tiffCmd", "").setCmdPath(tiffUnCompressor).setInputPath(tiffInput).setOutputPath(tiffOutput).assemble();
            Process tiffProcess = tiffPb.start();
            tiffProcess.waitFor();
            tiffProcess.exitValue();
            
            // Step 2: generate and store JP2 image on DOSS
            String jp2Tool = jp2Generator.toString();
            // Note: hard coded path for testing for now
            Path jp2ImgPath = Paths.get("/doss-devel/dlir/doss/blob/1042.jp2");
            ProcessBuilder jp2Pb = new ProcessBuilder(jp2Tool, "");
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            jp2Process.exitValue();
            return jp2ImgPath;
        }
    }
}
