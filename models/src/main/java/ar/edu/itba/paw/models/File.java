package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "media")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq")
    @SequenceGenerator(sequenceName = "media_id_seq", name = "media_id_seq", allocationSize = 1)
    @Column(name ="id")
    private long imageId;

    @Column(name = "bytes", nullable = false)
    private byte[] file;


    public File() {
        // HIBERNATE ONLY
    }

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
