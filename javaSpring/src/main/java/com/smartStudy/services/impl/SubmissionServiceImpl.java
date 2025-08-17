package com.smartStudy.services.impl;
import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseSubmission;
import com.smartStudy.pojo.Student;
import com.smartStudy.repositories.ExerciseRepository;
import com.smartStudy.repositories.SubmissionRepository;
import com.smartStudy.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepo;
    @Autowired
    private ExerciseRepository exerciseRepo;


    @Override
    public List<ExerciseSubmission> getExerciseSubmission(Map<String, String> params) {
        return this.submissionRepo.getExerciseSubmission(params);
    }

    @Override
    public ExerciseSubmission findById(Integer id) {
        return this.submissionRepo.findById(id);
    }

    @Override
    public List<ExerciseSubmission> findByExercise(Integer exerciseId, Integer studentId, String status) {
        return this.submissionRepo.findByExercise(exerciseId,studentId,status);
    }

    @Override
    public ExerciseSubmission create(ExerciseSubmission es, Integer exerciseId, Integer studentId) {
        Exercise exercise = this.exerciseRepo.findById(exerciseId);
        Student student = new Student();
        es.setExerciseId(exercise);
        es.setStudent(student);
        return this.submissionRepo.save(es);
    }

    @Override
    public ExerciseSubmission update(Integer id, ExerciseSubmission es, Integer exerciseId, Integer studentId) {
        ExerciseSubmission existing = submissionRepo.findById(id);
        if(existing != null)
        {
            Exercise exercise = this.exerciseRepo.findById(exerciseId);
            Student student = new Student();
            student.setUserId(studentId);
            existing.setExerciseId(exercise);
            existing.setStudent(student);
            existing.setFeedback(es.getFeedback());
            existing.setGrade(es.getGrade());
            existing.setStatus(es.getStatus());
            existing.setSubmittedAt(es.getSubmittedAt());
            return this.submissionRepo.save(es);
        }
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        this.submissionRepo.deleteById(id);
    }
}
