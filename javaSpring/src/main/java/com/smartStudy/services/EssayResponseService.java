package com.smartStudy.services;
import com.smartStudy.pojo.EssayResponse;
import java.util.List;

public interface EssayResponseService {
    List<EssayResponse> findBySubmission(Integer submissionId);
    EssayResponse findOne(Integer submissionId, Integer questionId);

    /**
     * Upsert nội dung bài làm tự luận cho 1 câu trong 1 submission.
     * Validate: question thuộc exercise của submission; exercise phải là ESSAY.
     */
    EssayResponse upsert(Integer submissionId, Integer questionId, String answerEssay);

    void deleteOne(Integer submissionId, Integer questionId);
    void deleteBySubmission(Integer submissionId);
}
