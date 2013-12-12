package amberdb.model;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.tools.ant.util.FileUtils;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.utils.OSProcessBuilder;
import amberdb.utils.images.ImgConvertBuilder;
import amberdb.utils.images.JP2EchoBuilder;
import amberdb.utils.images.KduCompressBuilder;
import amberdb.utils.images.TiffCPBuilder;
import amberdb.utils.images.TiffEchoBuilder;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

import doss.Blob;
import doss.BlobStore;
import doss.local.LocalBlobStore;

public interface MasterImageCopy extends Copy {
    @JavaHandler
    Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator, Path stagingPath) throws IllegalStateException, IOException, InterruptedException;
    
    abstract class Impl implements JavaHandlerContext<Vertex>, MasterImageCopy {
        @Override
        public Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator, Path stagingPath) throws IllegalStateException, IOException, InterruptedException {
            File masterImage = this.getFile();
            if (masterImage == null || masterImage.getBlobId() == null) 
                throw new NoSuchCopyException(getWork().getId(), CopyRole.MASTER_COPY);
            
            // set the masterImage file to have file extension of .tif
            LocalBlobStore doss = (LocalBlobStore) AmberSession.ownerOf(g()).getBlobStore();
            try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) doss.begin()) {
                tx.setExtension(masterImage.getBlobId(), ".tif");    
                tx.commit();
                
                Path jp2ImgPath = generateImage(doss, tiffUnCompressor, jp2Generator, stagingPath, masterImage);
               
                if (jp2ImgPath != null) {
                    Work work = this.getWork();
                    Copy ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "jp2");
                    AccessImageFile acf = (AccessImageFile) ac.getFile();
                    acf.setLocation(jp2ImgPath.toString());
                    return ac;
                }
                return null;
            }
        }
        
        private Path generateImage(LocalBlobStore doss, Path tiffUnCompressor, Path jp2Generator, Path stagingPath, File masterImage) throws IOException, InterruptedException, NoSuchCopyException {
            if (masterImage == null || masterImage.getBlobId() == null)
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.MASTER_COPY);
            
            // Step 1: uncompress Tiff
            OSProcessBuilder tiffUtil = getTiffProcessBuilder(tiffUnCompressor);
            
            // NOTE: setting staging path to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!stagingPath.toFile().exists())
                stagingPath = Paths.get(".");
            
            Path tiffInput = stagingPath.resolve(masterImage.getBlobId().toString() + ".tif"); 
            Path tiffOutput = stagingPath.resolve("uncompressed").resolve(masterImage.getBlobId().toString() + ".tif");
            stageTiffInput(tiffInput, doss.get(masterImage.getBlobId()));
            
            ProcessBuilder tiffPb = tiffUtil.setCmdPath(tiffUnCompressor).setInputPath(tiffInput).setOutputPath(tiffOutput).assemble();
            Process tiffProcess = tiffPb.start();
            tiffProcess.waitFor();
            tiffProcess.exitValue();
            
            // Step 2: generate and store JP2 image on DOSS
            OSProcessBuilder jp2Util = getJP2ProcessBuilder(jp2Generator);
            // Note: hard coded path for testing for now
            Path jp2ImgPath = stagingPath.resolve("jp2").resolve(masterImage.getBlobId().toString() + ".jp2");
            // Path jp2ImgPath = Paths.get("/doss-devel/dlir/doss/blob/1042.jp2");
            ProcessBuilder jp2Pb = jp2Util.setCmdPath(jp2Generator).setInputPath(tiffOutput).setOutputPath(jp2ImgPath).assemble();
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            jp2Process.exitValue();
            
            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!tiffUnCompressor.toFile().exists() || !jp2Generator.toFile().exists()) return null;
            return jp2ImgPath;
        }
        
        private OSProcessBuilder getTiffProcessBuilder(Path tiffUnCompressor) {
            OSProcessBuilder tiffUtil = null;
            if (!tiffUnCompressor.toFile().exists()) {
                tiffUtil = new TiffEchoBuilder();
            } else {
                String tiffUncompressorPath = tiffUnCompressor.toString();
                if (tiffUncompressorPath.contains("tiffcp")) {
                    tiffUtil = new TiffCPBuilder();
                } else {
                    tiffUtil = new ImgConvertBuilder();
                }
            }
            return tiffUtil;
        }
        
        private OSProcessBuilder getJP2ProcessBuilder(Path jp2Generator) {
            OSProcessBuilder jp2Util = null;
            if (!jp2Generator.toFile().exists()) {
                jp2Util = new JP2EchoBuilder();
            } else {
                jp2Util = new KduCompressBuilder();
            }
            return jp2Util;
        }
        
        private void stageTiffInput(Path tiffInput, Blob tiff) throws IOException {
            ReadableByteChannel channel = tiff.openChannel();
            FileChannel dest = FileChannel.open(tiffInput,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            long bytesTransferred = dest.transferFrom(channel, 0,
                    Long.MAX_VALUE);
        }
    }
}
