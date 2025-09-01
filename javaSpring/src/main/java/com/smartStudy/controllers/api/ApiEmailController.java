// src/main/java/com/smartStudy/controllers/ApiEmailController.java
package com.smartStudy.controllers.api;

import com.smartStudy.services.EmailService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;
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
    @PostMapping("/remind/today")
    public ResponseEntity<Void> remindToday() {
        emailService.remindStudy(LocalDate.now());
        return ResponseEntity.accepted().build(); // 202 Accepted
    }

    // 2) Gửi nhắc cho TẤT CẢ học sinh vào NGÀY chỉ định
    // POST /api/emails/remind?date=2025-09-02
    @PostMapping("/remind")
    public ResponseEntity<Void> remindByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        emailService.remindStudy(d);
        return ResponseEntity.accepted().build();
    }

    // 3) (Tuỳ chọn) Gửi nhắc CHO 1 HỌC SINH cụ thể vào NGÀY chỉ định/hoặc hôm nay
    // POST /api/emails/remind/student/64            -> today
    // POST /api/emails/remind/student/64?date=...   -> date
    @PostMapping("/remind/student/{studentId}")
    public ResponseEntity<Void> remindForStudent(
            @PathVariable Integer studentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        emailService.remindStudy(studentId, d);
        return ResponseEntity.accepted().build();
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

