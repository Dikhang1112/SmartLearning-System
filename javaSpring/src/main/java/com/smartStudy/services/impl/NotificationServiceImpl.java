package com.smartStudy.services.impl;

import com.smartStudy.pojo.Notification;
import com.smartStudy.repositories.NotificationRepository;
import com.smartStudy.services.NotifcationService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotifcationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private LocalSessionFactoryBean factoryBean;
    private Session session() {
        return factoryBean.getObject().getCurrentSession();
    }

    @Override
    public Notification saveOrUpdate(Notification no) {
        return this.notificationRepository.addOrUpdate(no);
    }

    @Override
    public List<Notification> getNotifications(Map<String, String> params) {
        return this.notificationRepository.getNotifications(params);
    }

    @Override
    @Transactional
    public void markAllRead(int studentId) {
        if (studentId <= 0)
            throw new IllegalArgumentException("studentId must be positive");
        // ✅ Set isReaded=true cho toàn bộ thông báo của student
        String hql = """
            update Notification n
               set n.isReaded = true
             where n.studentId.userId = :sid
               and (n.isReaded is null or n.isReaded = false)
            """;
        session().createQuery(hql)
                .setParameter("sid", studentId)
                .executeUpdate();
    }
}
