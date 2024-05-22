package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "media")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq")
    @SequenceGenerator(sequenceName = "media_id_seq", name = "media_id_seq", allocationSize = 1)
    @Column(name ="id")
    private Long imageId;

    @Column(name = "bytes", nullable = false)
    private Byte[] file;


    public File() {
        // HIBERNATE ONLY
    }

    public File(Byte[] file) {
        this.imageId = imageId;
        this.file = file;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public Byte[] getFile() {
        return file;
    }

    public void setFile(Byte[] file) {
        this.file = file;
    }
}
