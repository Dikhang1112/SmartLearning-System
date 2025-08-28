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
}
