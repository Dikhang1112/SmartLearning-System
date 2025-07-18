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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "chapter_progress")
@NamedQueries({
    @NamedQuery(name = "ChapterProgress.findAll", query = "SELECT c FROM ChapterProgress c"),
    @NamedQuery(name = "ChapterProgress.findById", query = "SELECT c FROM ChapterProgress c WHERE c.id = :id"),
    @NamedQuery(name = "ChapterProgress.findByCompleted", query = "SELECT c FROM ChapterProgress c WHERE c.completed = :completed"),
    @NamedQuery(name = "ChapterProgress.findByLastScore", query = "SELECT c FROM ChapterProgress c WHERE c.lastScore = :lastScore"),
    @NamedQuery(name = "ChapterProgress.findByUpdatedAt", query = "SELECT c FROM ChapterProgress c WHERE c.updatedAt = :updatedAt")})
public class ChapterProgress implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "completed")
    private Boolean completed;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "last_score")
    private BigDecimal lastScore;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Chapters chapterId;
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users studentId;

    public ChapterProgress() {
    }

    public ChapterProgress(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public BigDecimal getLastScore() {
        return lastScore;
    }

    public void setLastScore(BigDecimal lastScore) {
        this.lastScore = lastScore;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Chapters getChapterId() {
        return chapterId;
    }

    public void setChapterId(Chapters chapterId) {
        this.chapterId = chapterId;
    }

    public Users getStudentId() {
        return studentId;
    }

    public void setStudentId(Users studentId) {
        this.studentId = studentId;
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
        if (!(object instanceof ChapterProgress)) {
            return false;
        }
        ChapterProgress other = (ChapterProgress) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ChapterProgress[ id=" + id + " ]";
    }
    
}
