/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "chapter_attachments")
@NamedQueries({
    @NamedQuery(name = "ChapterAttachments.findAll", query = "SELECT c FROM ChapterAttachments c"),
    @NamedQuery(name = "ChapterAttachments.findById", query = "SELECT c FROM ChapterAttachments c WHERE c.id = :id"),
    @NamedQuery(name = "ChapterAttachments.findByType", query = "SELECT c FROM ChapterAttachments c WHERE c.type = :type"),
    @NamedQuery(name = "ChapterAttachments.findByFilename", query = "SELECT c FROM ChapterAttachments c WHERE c.filename = :filename"),
    @NamedQuery(name = "ChapterAttachments.findByFilepath", query = "SELECT c FROM ChapterAttachments c WHERE c.filepath = :filepath"),
    @NamedQuery(name = "ChapterAttachments.findByUploadedAt", query = "SELECT c FROM ChapterAttachments c WHERE c.uploadedAt = :uploadedAt")})
public class ChapterAttachments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "filename")
    private String filename;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "filepath")
    private String filepath;
    @Column(name = "uploaded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Chapters chapterId;

    public ChapterAttachments() {
    }

    public ChapterAttachments(Integer id) {
        this.id = id;
    }

    public ChapterAttachments(Integer id, String type, String filename, String filepath) {
        this.id = id;
        this.type = type;
        this.filename = filename;
        this.filepath = filepath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Chapters getChapterId() {
        return chapterId;
    }

    public void setChapterId(Chapters chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChapterAttachments)) {
            return false;
        }
        ChapterAttachments other = (ChapterAttachments) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ChapterAttachments[ id=" + id + " ]";
    }
    
}
