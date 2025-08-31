// com.smartStudy.services.EmailService.java
package com.smartStudy.services;

public interface EmailService {
    // thêm method mới
    void sendGradedNotice(String studentEmail,
                          String teacherName,
                          String teacherEmail,
                          String exerciseTitle,
                          Long submissionId,
                          String viewUrl,
                          Integer grade,
                          String feedback);
    void sendPlainText(String to, String subject, String content);
    void sendOtpEmail(String to, String otp, long ttlMinutes);

}
