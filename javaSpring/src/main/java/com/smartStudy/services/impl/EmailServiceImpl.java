package com.smartStudy.services.impl;

import com.smartStudy.services.EmailService;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String systemFrom;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String feUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendGradedNotice(String studentEmail,
                                 String teacherName,
                                 String teacherEmail,
                                 String exerciseTitle,
                                 Long submissionId,
                                 String viewUrl,
                                 Integer grade,
                                 String feedback) {
        try {
            var msg = mailSender.createMimeMessage();
            var h = new MimeMessageHelper(msg, "UTF-8");

            // From = tài khoản hệ thống, tên hiển thị = Teacher via SmartStudy
            h.setFrom(new InternetAddress(systemFrom, (teacherName == null || teacherName.isBlank()
                    ? "Teacher" : teacherName) + " via SmartStudy"));

            // Student nhận mail; Reply-To về teacher
            if (teacherEmail != null && !teacherEmail.isBlank()) h.setReplyTo(teacherEmail);
            h.setTo(studentEmail);

            String subject = "[SmartStudy] Bài của bạn đã được chấm: " + exerciseTitle;
            h.setSubject(subject);

            String link = (viewUrl == null || viewUrl.isBlank())
                    ? feUrl + "/student/submissions/" + (submissionId == null ? "" : submissionId)
                    : viewUrl;

            String gradeLine = (grade != null) ? "<li>Điểm: <b>" + grade + "</b></li>" : "";
            String feedbackBlock = (feedback != null && !feedback.isBlank())
                    ? "<p><b>Nhận xét:</b><br/>" + feedback + "</p>" : "";

            String html = """
            <p>Xin chào,</p>
            <p>Thầy/Cô <b>%s</b> đã <b>chấm bài</b> <b>%s</b> của bạn.</p>
            <ul>
              <li>Mã bài nộp: <b>%s</b></li>
              %s
            </ul>
            %s
            <p>Trân trọng,<br/>SmartStudy</p>
        """.formatted(
                    (teacherName == null || teacherName.isBlank()) ? "Giáo viên" : teacherName,
                    (exerciseTitle == null || exerciseTitle.isBlank()) ? "Bài tập" : exerciseTitle,
                    (submissionId == null ? "" : submissionId.toString()),
                    gradeLine,
                    feedbackBlock,
                    link, link
            );

            h.setText(html, true);
            mailSender.send(msg);
        } catch (Exception ignored) {
            // TODO: log cảnh báo nếu cần
        }
    }
}

