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
    private byte[] file;


    public File() {
        // HIBERNATE ONLY
    }

    public File(byte[] file) {
        this.file = file;
    }
    public File(Long id, byte[] file) {
        this.imageId = id;
        this.file = file;
    }
    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
