package ar.edu.itba.paw.models;

public class File {

    private long imageId;

    private byte[] file;

    public File(long imageId, byte[] file) {
        this.imageId = imageId;
        this.file = file;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
