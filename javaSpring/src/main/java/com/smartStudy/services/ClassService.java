package com.smartStudy.services;

import com.smartStudy.pojo.Class;

import java.util.List;
import java.util.Map;

public interface ClassService {
    List<Class> getCLasses (Map<String,String> params);
    Class getClassById(int id);
    Class addOrUpdate(Class c);
    void deleteClass(int id);
    Integer totalStudentClass(Integer classId);
    Integer totalTeacherClass(Integer classId);
}
