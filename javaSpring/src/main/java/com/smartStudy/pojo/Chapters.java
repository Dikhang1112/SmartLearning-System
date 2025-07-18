/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "chapters")
@NamedQueries({
    @NamedQuery(name = "Chapters.findAll", query = "SELECT c FROM Chapters c"),
    @NamedQuery(name = "Chapters.findById", query = "SELECT c FROM Chapters c WHERE c.id = :id"),
    @NamedQuery(name = "Chapters.findByTitle", query = "SELECT c FROM Chapters c WHERE c.title = :title"),
    @NamedQuery(name = "Chapters.findByOrderIndex", query = "SELECT c FROM Chapters c WHERE c.orderIndex = :orderIndex"),
    @NamedQuery(name = "Chapters.findByCreatedAt", query = "SELECT c FROM Chapters c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Chapters.findByUpdatedAt", query = "SELECT c FROM Chapters c WHERE c.updatedAt = :updatedAt")})
public class Chapters implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "summary_text")
    private String summaryText;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_index")
    private int orderIndex;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    private List<ChapterProgress> chapterProgressList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    private List<Notes> notesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    private List<Exercises> exercisesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    private List<ChapterAttachments> chapterAttachmentsList;
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Subjects subjectId;

    public Chapters() {
    }

    public Chapters(Integer id) {
        this.id = id;
    }

    public Chapters(Integer id, String title, int orderIndex) {
        this.id = id;
        this.title = title;
        this.orderIndex = orderIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ChapterProgress> getChapterProgressList() {
        return chapterProgressList;
    }

    public void setChapterProgressList(List<ChapterProgress> chapterProgressList) {
        this.chapterProgressList = chapterProgressList;
    }

    public List<Notes> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Notes> notesList) {
        this.notesList = notesList;
    }

    public List<Exercises> getExercisesList() {
        return exercisesList;
    }

    public void setExercisesList(List<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
    }

    public List<ChapterAttachments> getChapterAttachmentsList() {
        return chapterAttachmentsList;
    }

    public void setChapterAttachmentsList(List<ChapterAttachments> chapterAttachmentsList) {
        this.chapterAttachmentsList = chapterAttachmentsList;
    }

    public Subjects getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Subjects subjectId) {
        this.subjectId = subjectId;
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
        if (!(object instanceof Chapters)) {
            return false;
        }
        Chapters other = (Chapters) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Chapters[ id=" + id + " ]";
    }
    
}
