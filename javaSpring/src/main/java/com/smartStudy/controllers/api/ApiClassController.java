package com.smartStudy.controllers.api;

import com.smartStudy.pojo.Class;
import com.smartStudy.pojo.Subject;
import com.smartStudy.services.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/classes")
    public ResponseEntity <List<Class>> classList (@RequestParam Map<String,String> params)
    {
        List<Class> classes = classService.getCLasses(params);
        classes.forEach(c -> {
            String teacherNames = c.getTeacherList() != null && !c.getTeacherList().isEmpty()
                    ? c.getTeacherList().stream()
                    .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No teachers assigned";
            c.setTeacherNames(teacherNames);
        });

        classes.forEach(c -> {
            String studentNames = c.getStudentList() != null && !c.getStudentList().isEmpty()
                    ? c.getStudentList().stream()
                    .map(student -> student.getUser() != null ? student.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No students study";
            c.setStudentNames(studentNames);
        });
        return ResponseEntity.ok(classes);
    }

    @GetMapping("classes/{classId}")
    public ResponseEntity<Class> getClassById( @PathVariable (value = "classId") int id)
    {
        return new  ResponseEntity<> (this.classService.getClassById(id),HttpStatus.OK);
    }
}
