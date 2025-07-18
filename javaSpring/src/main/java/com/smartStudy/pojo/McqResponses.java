/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "mcq_responses")
@NamedQueries({
    @NamedQuery(name = "McqResponses.findAll", query = "SELECT m FROM McqResponses m"),
    @NamedQuery(name = "McqResponses.findBySubmissionId", query = "SELECT m FROM McqResponses m WHERE m.mcqResponsesPK.submissionId = :submissionId"),
    @NamedQuery(name = "McqResponses.findByQuestionId", query = "SELECT m FROM McqResponses m WHERE m.mcqResponsesPK.questionId = :questionId")})
public class McqResponses implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected McqResponsesPK mcqResponsesPK;
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ExerciseAnswers answerId;
    @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseQuestions exerciseQuestions;
    @JoinColumn(name = "submission_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseSubmissions exerciseSubmissions;

    public McqResponses() {
    }

    public McqResponses(McqResponsesPK mcqResponsesPK) {
        this.mcqResponsesPK = mcqResponsesPK;
    }

    public McqResponses(int submissionId, int questionId) {
        this.mcqResponsesPK = new McqResponsesPK(submissionId, questionId);
    }

    public McqResponsesPK getMcqResponsesPK() {
        return mcqResponsesPK;
    }

    public void setMcqResponsesPK(McqResponsesPK mcqResponsesPK) {
        this.mcqResponsesPK = mcqResponsesPK;
    }

    public ExerciseAnswers getAnswerId() {
        return answerId;
    }

    public void setAnswerId(ExerciseAnswers answerId) {
        this.answerId = answerId;
    }

    public ExerciseQuestions getExerciseQuestions() {
        return exerciseQuestions;
    }

    public void setExerciseQuestions(ExerciseQuestions exerciseQuestions) {
        this.exerciseQuestions = exerciseQuestions;
    }

    public ExerciseSubmissions getExerciseSubmissions() {
        return exerciseSubmissions;
    }

    public void setExerciseSubmissions(ExerciseSubmissions exerciseSubmissions) {
        this.exerciseSubmissions = exerciseSubmissions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mcqResponsesPK != null ? mcqResponsesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof McqResponses)) {
            return false;
        }
        McqResponses other = (McqResponses) object;
        if ((this.mcqResponsesPK == null && other.mcqResponsesPK != null) || (this.mcqResponsesPK != null && !this.mcqResponsesPK.equals(other.mcqResponsesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.McqResponses[ mcqResponsesPK=" + mcqResponsesPK + " ]";
    }
    
}
