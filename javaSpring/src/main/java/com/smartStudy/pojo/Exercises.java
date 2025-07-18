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
@Table(name = "exercises")
@NamedQueries({
    @NamedQuery(name = "Exercises.findAll", query = "SELECT e FROM Exercises e"),
    @NamedQuery(name = "Exercises.findById", query = "SELECT e FROM Exercises e WHERE e.id = :id"),
    @NamedQuery(name = "Exercises.findByTitle", query = "SELECT e FROM Exercises e WHERE e.title = :title"),
    @NamedQuery(name = "Exercises.findByType", query = "SELECT e FROM Exercises e WHERE e.type = :type"),
    @NamedQuery(name = "Exercises.findByCreatedAt", query = "SELECT e FROM Exercises e WHERE e.createdAt = :createdAt")})
public class Exercises implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 200)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "type")
    private String type;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseId")
    private List<ExerciseSubmissions> exerciseSubmissionsList;
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Chapters chapterId;
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users createdBy;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseId")
    private List<ExerciseQuestions> exerciseQuestionsList;

    public Exercises() {
    }

    public Exercises(Integer id) {
        this.id = id;
    }

    public Exercises(Integer id, String type) {
        this.id = id;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<ExerciseSubmissions> getExerciseSubmissionsList() {
        return exerciseSubmissionsList;
    }

    public void setExerciseSubmissionsList(List<ExerciseSubmissions> exerciseSubmissionsList) {
        this.exerciseSubmissionsList = exerciseSubmissionsList;
    }

    public Chapters getChapterId() {
        return chapterId;
    }

    public void setChapterId(Chapters chapterId) {
        this.chapterId = chapterId;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public List<ExerciseQuestions> getExerciseQuestionsList() {
        return exerciseQuestionsList;
    }

    public void setExerciseQuestionsList(List<ExerciseQuestions> exerciseQuestionsList) {
        this.exerciseQuestionsList = exerciseQuestionsList;
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
        if (!(object instanceof Exercises)) {
            return false;
        }
        Exercises other = (Exercises) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Exercises[ id=" + id + " ]";
    }
    
}
