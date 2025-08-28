package com.smartStudy.services;

import com.smartStudy.pojo.Notification;

import java.util.List;
import java.util.Map;

public interface NotifcationService {
    Notification saveOrUpdate (Notification no);
    List<Notification> getNotifications (Map<String,String> params);
    void markAllRead(int studentId);
}
