package com.smartStudy.services.impl;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.repositories.ExerciseRepository;
import com.smartStudy.services.ExcerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExcerciseServiceImpl implements ExcerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Override
    public List<Exercise> getExercises(Map<String, String> params) {
        return this.exerciseRepository.getExercises(params);
    }

    @Override
    public long countExercises(Map<String, String> params) {
        return this.exerciseRepository.countExercises(params);
    }

    @Override
    public Exercise get(Integer id) {
        return this.exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> findByChapterId(Integer cid) {
        return this.exerciseRepository.findByChapterId(cid);
    }

    @Override
    public Exercise create(Exercise ex) {
        return this.exerciseRepository.save(ex);
    }

    @Override
    public Exercise update(Integer id, Exercise ex) {
        Exercise old = exerciseRepository.findById(id);
        if(old == null) return null;
        if(old.getTitle() != null) old.setTitle(ex.getTitle());
        if(old.getDescription() != null) old.setDescription(ex.getDescription());
        return  this.exerciseRepository.save(old);
    }

    @Override
    public void delete(Integer id) {
        this.exerciseRepository.deleteById(id);
    }
}
