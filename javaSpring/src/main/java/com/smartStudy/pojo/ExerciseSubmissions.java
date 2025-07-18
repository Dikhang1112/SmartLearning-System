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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "exercise_submissions")
@NamedQueries({
    @NamedQuery(name = "ExerciseSubmissions.findAll", query = "SELECT e FROM ExerciseSubmissions e"),
    @NamedQuery(name = "ExerciseSubmissions.findById", query = "SELECT e FROM ExerciseSubmissions e WHERE e.id = :id"),
    @NamedQuery(name = "ExerciseSubmissions.findBySubmittedAt", query = "SELECT e FROM ExerciseSubmissions e WHERE e.submittedAt = :submittedAt"),
    @NamedQuery(name = "ExerciseSubmissions.findByGrade", query = "SELECT e FROM ExerciseSubmissions e WHERE e.grade = :grade")})
public class ExerciseSubmissions implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "submitted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "grade")
    private BigDecimal grade;
    @Lob
    @Size(max = 65535)
    @Column(name = "feedback")
    private String feedback;
    @Lob
    @Size(max = 65535)
    @Column(name = "essay_answer")
    private String essayAnswer;
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Exercises exerciseId;
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users studentId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseSubmissions")
    private List<McqResponses> mcqResponsesList;

    public ExerciseSubmissions() {
    }

    public ExerciseSubmissions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEssayAnswer() {
        return essayAnswer;
    }

    public void setEssayAnswer(String essayAnswer) {
        this.essayAnswer = essayAnswer;
    }

    public Exercises getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Exercises exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Users getStudentId() {
        return studentId;
    }

    public void setStudentId(Users studentId) {
        this.studentId = studentId;
    }

    public List<McqResponses> getMcqResponsesList() {
        return mcqResponsesList;
    }

    public void setMcqResponsesList(List<McqResponses> mcqResponsesList) {
        this.mcqResponsesList = mcqResponsesList;
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
        if (!(object instanceof ExerciseSubmissions)) {
            return false;
        }
        ExerciseSubmissions other = (ExerciseSubmissions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ExerciseSubmissions[ id=" + id + " ]";
    }
    
}
