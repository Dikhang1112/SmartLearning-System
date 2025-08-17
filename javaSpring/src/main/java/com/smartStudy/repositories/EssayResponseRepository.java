package com.smartStudy.repositories;

import com.smartStudy.pojo.EssayResponse;
import java.util.List;

public interface EssayResponseRepository {
    List<EssayResponse> findBySubmission(Integer submissionId);
    EssayResponse findOne(Integer submissionId, Integer questionId);
    EssayResponse upsert(Integer submissionId, Integer questionId, String answerEssay);
    void deleteOne(Integer submissionId, Integer questionId);
    void deleteBySubmission(Integer submissionId);
}
