package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@Table(name="el_file")
public class ElFile extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotEmpty(message = "No file name")
    @Column(name="file_name", nullable = false)
    private String fileName;

    @NotEmpty(message = "No save name")
    @Column(name="save_name", nullable = false)
    private String saveName;

    @Column(name="size")
    private long size;

    @Column(name="mime_type")
    private String mimeType;

    public ElFile() {
    }

    public ElFile(String fileName, String saveName, long size, String mimeType) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "UploadFile [id=" + id + ", fileName=" + fileName + ", saveName=" + saveName + ", size=" + size + ", mimeType=" + mimeType
                + ", insertBy=" + getCreatedBy() + "]"
                + ", insertDate=" + getCreatedDate() + "]";
    }
}
