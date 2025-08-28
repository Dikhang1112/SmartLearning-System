// src/main/java/com/smartStudy/controllers/ApiEmailController.java
package com.smartStudy.controllers.api;

import com.smartStudy.services.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class ApiEmailController {

    private final EmailService emailService;
    public ApiEmailController(EmailService emailService) { this.emailService = emailService; }

    // Teacher -> Student (đã chấm bài)
    @PostMapping(value = "/send", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> send(@RequestBody SendGradedNoticeRequest req) {
        emailService.sendGradedNotice(
                req.getStudentEmail(),
                req.getTeacherName(),
                req.getTeacherEmail(),
                req.getExerciseTitle(),
                req.getSubmissionId(),
                req.getViewUrl(),
                req.getGrade(),
                req.getFeedback()
        );
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "to", req.getStudentEmail(),
                "replyTo", req.getTeacherEmail()
        ));
    }

    // Body khớp tham số method mới
    public static class SendGradedNoticeRequest {
        private String studentEmail;
        private String teacherName;
        private String teacherEmail;
        private String exerciseTitle;
        private Long submissionId;
        private String viewUrl;
        private Integer grade;
        private String feedback;

        public String getStudentEmail() {
            return studentEmail;
        }

        public void setStudentEmail(String studentEmail) {
            this.studentEmail = studentEmail;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getTeacherEmail() {
            return teacherEmail;
        }

        public void setTeacherEmail(String teacherEmail) {
            this.teacherEmail = teacherEmail;
        }

        public String getExerciseTitle() {
            return exerciseTitle;
        }

        public void setExerciseTitle(String exerciseTitle) {
            this.exerciseTitle = exerciseTitle;
        }

        public Long getSubmissionId() {
            return submissionId;
        }

        public void setSubmissionId(Long submissionId) {
            this.submissionId = submissionId;
        }

        public String getViewUrl() {
            return viewUrl;
        }

        public void setViewUrl(String viewUrl) {
            this.viewUrl = viewUrl;
        }

        public Integer getGrade() {
            return grade;
        }

        public void setGrade(Integer grade) {
            this.grade = grade;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }
}

