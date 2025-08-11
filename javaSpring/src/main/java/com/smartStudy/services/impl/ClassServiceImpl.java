package com.smartStudy.services.impl;

import com.smartStudy.pojo.Class;
import com.smartStudy.repositories.ClassRepository;
import com.smartStudy.services.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Override
    public List<Class> getCLasses(Map<String, String> params) {
        return this.classRepository.getCLasses(params);
    }

    @Override
    public Class getClassById(int id) {
        return this.classRepository.getClassById(id);
    }

    @Override
    public Class addOrUpdate(Class c) {
        Class existingClass = null;
        if (c.getId() != null) {
            existingClass= this.classRepository.getClassById(c.getId());
        }
        // Xử lý createdAt và updatedAt
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());

        if (c.getId() == null) {
            // Khi add mới: đặt cả createdAt và updatedAt giống nhau
            c.setCreatedAt(currentDate);
            c.setUpdatedAt(currentDate);
        } else {
            // Khi update: chỉ cập nhật updatedAt, giữ nguyên createdAt
            c.setUpdatedAt(currentDate);
            if (existingClass != null) {
                c.setCreatedAt(existingClass.getCreatedAt());
            }
        }
        return this.classRepository.addOrUpdate(c);
    }

    @Override
    public void deleteClass(int id)
    {
        this.classRepository.deleteClass(id);
    }

    @Override
    public Integer totalStudentClass(Integer classId) {
        return this.classRepository.totalStudentClass(classId);
    }

    @Override
    public Integer totalTeacherClass(Integer classId) {
        return this.classRepository.totalTeacherClass(classId);
    }
}
