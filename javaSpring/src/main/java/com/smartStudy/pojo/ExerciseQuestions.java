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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "exercise_questions")
@NamedQueries({
    @NamedQuery(name = "ExerciseQuestions.findAll", query = "SELECT e FROM ExerciseQuestions e"),
    @NamedQuery(name = "ExerciseQuestions.findById", query = "SELECT e FROM ExerciseQuestions e WHERE e.id = :id"),
    @NamedQuery(name = "ExerciseQuestions.findByOrderIndex", query = "SELECT e FROM ExerciseQuestions e WHERE e.orderIndex = :orderIndex")})
public class ExerciseQuestions implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "question")
    private String question;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_index")
    private int orderIndex;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseQuestions")
    private List<McqResponses> mcqResponsesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionId")
    private List<ExerciseAnswers> exerciseAnswersList;
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Exercises exerciseId;

    public ExerciseQuestions() {
    }

    public ExerciseQuestions(Integer id) {
        this.id = id;
    }

    public ExerciseQuestions(Integer id, String question, int orderIndex) {
        this.id = id;
        this.question = question;
        this.orderIndex = orderIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<McqResponses> getMcqResponsesList() {
        return mcqResponsesList;
    }

    public void setMcqResponsesList(List<McqResponses> mcqResponsesList) {
        this.mcqResponsesList = mcqResponsesList;
    }

    public List<ExerciseAnswers> getExerciseAnswersList() {
        return exerciseAnswersList;
    }

    public void setExerciseAnswersList(List<ExerciseAnswers> exerciseAnswersList) {
        this.exerciseAnswersList = exerciseAnswersList;
    }

    public Exercises getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Exercises exerciseId) {
        this.exerciseId = exerciseId;
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
        if (!(object instanceof ExerciseQuestions)) {
            return false;
        }
        ExerciseQuestions other = (ExerciseQuestions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ExerciseQuestions[ id=" + id + " ]";
    }
    
}
