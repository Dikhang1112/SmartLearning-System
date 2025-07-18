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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "exercise_answers")
@NamedQueries({
    @NamedQuery(name = "ExerciseAnswers.findAll", query = "SELECT e FROM ExerciseAnswers e"),
    @NamedQuery(name = "ExerciseAnswers.findById", query = "SELECT e FROM ExerciseAnswers e WHERE e.id = :id"),
    @NamedQuery(name = "ExerciseAnswers.findByAnswerText", query = "SELECT e FROM ExerciseAnswers e WHERE e.answerText = :answerText"),
    @NamedQuery(name = "ExerciseAnswers.findByIsCorrect", query = "SELECT e FROM ExerciseAnswers e WHERE e.isCorrect = :isCorrect")})
public class ExerciseAnswers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "answer_text")
    private String answerText;
    @Column(name = "is_correct")
    private Boolean isCorrect;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "answerId")
    private List<McqResponses> mcqResponsesList;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ExerciseQuestions questionId;

    public ExerciseAnswers() {
    }

    public ExerciseAnswers(Integer id) {
        this.id = id;
    }

    public ExerciseAnswers(Integer id, String answerText) {
        this.id = id;
        this.answerText = answerText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public List<McqResponses> getMcqResponsesList() {
        return mcqResponsesList;
    }

    public void setMcqResponsesList(List<McqResponses> mcqResponsesList) {
        this.mcqResponsesList = mcqResponsesList;
    }

    public ExerciseQuestions getQuestionId() {
        return questionId;
    }

    public void setQuestionId(ExerciseQuestions questionId) {
        this.questionId = questionId;
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
        if (!(object instanceof ExerciseAnswers)) {
            return false;
        }
        ExerciseAnswers other = (ExerciseAnswers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ExerciseAnswers[ id=" + id + " ]";
    }
    
}
