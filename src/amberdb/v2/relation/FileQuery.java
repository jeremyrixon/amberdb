package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.model.*;
import amberdb.v2.relation.model.CopyFile;

public class FileQuery extends AmberQueryBase {

    public FileQuery(AmberSession session) {
        super(session);
    }

    public File getFile(Long copyId) {
        CopyFile cf = session.getCopyFileDao().getFileByCopyId(copyId);

        if ("ImageFile".equalsIgnoreCase(cf.getFileType())) {
            return session.getImageFileDao().get(cf.getFileId());
        }

        return null;
    }

    public SoundFile getSoundFile(Long copyId) {
        CopyFile cf = session.getCopyFileDao().getFileByType(copyId, "SoundFile");

        if (cf != null) {
            return session.getSoundFileDao().get(cf.getFileId());
        }

        return null;
    }

    public Copy getCopyFromSoundFile(Long fileId) {
        CopyFile cf = session.getCopyFileDao().getCopyByFileId(fileId);

        return session.getCopyDao().get(cf.getCopyId());
    }

    public Work getWorkForSoundFile(Long fileId) {
        Copy soundFileCopy = getCopyFromSoundFile(fileId);
        WorkCopyQuery cq = new WorkCopyQuery(session);
        return cq.getWork(soundFileCopy.getId());
    }

    public MovingImageFile getMovingImageFile(Long copyId) {
        CopyFile cf = session.getCopyFileDao().getFileByType(copyId, "MovingImageFile");

        if (cf != null) {
            return session.getMovingImageFileDao().get(cf.getFileId());
        }

        return null;
    }
}
