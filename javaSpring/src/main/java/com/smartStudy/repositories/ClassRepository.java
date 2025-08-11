package com.smartStudy.repositories;

import com.smartStudy.pojo.Class;

import java.util.List;
import java.util.Map;

public interface ClassRepository {
    List<Class> getCLasses (Map<String,String> params);

    Class getClassById(int id);
    Class addOrUpdate(Class c);
    void deleteClass(int id);
    Integer totalStudentClass(Integer classId);
    Integer totalTeacherClass(Integer classId);
}
