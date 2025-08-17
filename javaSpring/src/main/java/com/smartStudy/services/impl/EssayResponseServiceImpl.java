package com.smartStudy.services.impl;

import com.smartStudy.pojo.EssayResponse;
import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.pojo.ExerciseSubmission;
import com.smartStudy.repositories.EssayResponseRepository;
import com.smartStudy.services.EssayResponseService;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EssayResponseServiceImpl implements EssayResponseService {

    @Autowired
    private EssayResponseRepository repo;

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private Session currentSession() {
        return this.sessionFactory.getObject().getCurrentSession();
    }

    @Override
    public List<EssayResponse> findBySubmission(Integer submissionId) {
        return repo.findBySubmission(submissionId);
    }

    @Override
    public EssayResponse findOne(Integer submissionId, Integer questionId) {
        return repo.findOne(submissionId, questionId);
    }

    @Override
    public EssayResponse upsert(Integer submissionId, Integer questionId, String answerEssay) {
        if (submissionId == null || questionId == null)
            throw new IllegalArgumentException("submissionId, questionId are required");

        Session s = currentSession();

        ExerciseSubmission sub = s.get(ExerciseSubmission.class, submissionId);
        if (sub == null)
            throw new IllegalArgumentException("Submission not found: " + submissionId);

        ExerciseQuestion q = s.get(ExerciseQuestion.class, questionId);
        if (q == null)
            throw new IllegalArgumentException("Question not found: " + questionId);

        Exercise ex = sub.getExerciseId();
        if (ex == null || q.getExerciseId() == null || !ex.getId().equals(q.getExerciseId())) {
            throw new IllegalArgumentException("Question does not belong to this submission's exercise");
        }
        // Chặn nhầm loại bài
        if (ex.getType() != null && !"ESSAY".equalsIgnoreCase(ex.getType())) {
            throw new IllegalStateException("Exercise is not ESSAY; cannot save essay response");
        }

        return repo.upsert(submissionId, questionId, answerEssay);
    }

    @Override
    public void deleteOne(Integer submissionId, Integer questionId) {
        repo.deleteOne(submissionId, questionId);
    }

    @Override
    public void deleteBySubmission(Integer submissionId) {
        repo.deleteBySubmission(submissionId);
    }
}
