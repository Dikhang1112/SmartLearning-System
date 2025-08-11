package com.smartStudy.controllers;

import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;
import com.smartStudy.services.SubjectService;
import com.smartStudy.services.TeacherService;
import com.smartStudy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/subjects")
    public String subjectListView(Model model, @RequestParam(required = false) Map<String, String> params) {
        List<Subject> subjects = subjectService.getSubjects(params);
        // Tạo danh sách tên giáo viên cho mỗi môn học
        model.addAttribute("subjects", subjects.stream().map(subject -> {
            String teacherNames = subject.getTeacherList() != null && !subject.getTeacherList().isEmpty()
                    ? subject.getTeacherList().stream()
                    .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No teachers assigned";
            subject.setTeacherNames(teacherNames); // Giả sử Subject có thuộc tính teacherNames
            return subject;
        }).collect(Collectors.toList()));
        return "subjects";
    }
    @GetMapping("/subjects/add")
    public String addSubjectView(Model model) {
        model.addAttribute("subject", new Subject());
        return "editSubject";
    }
    @PostMapping("/subjects/add")
    public String addSubject(
            @ModelAttribute("subject") Subject subject,
            @RequestParam(value = "teacherIds", required = false) List<Integer> teacherIds,
            Model model
    ) {
        List<Teacher> teachers = new ArrayList<>();
        if (teacherIds != null) {
            for (Integer id : teacherIds) {
                Teacher t = teacherService.findByUserId(id);
                if (t != null) teachers.add(t);
            }
        }
        subject.setTeacherList(teachers);
        subjectService.addOrUpdate(subject);
        System.out.println("Teachers sẽ gán vào subject: " + teachers);
        return "redirect:/subjects";
    }




    @GetMapping("/subjects/{subjectId}")
    public String updateSubjectView(Model model, @PathVariable(value = "subjectId") int id) {
        model.addAttribute("subject", this.subjectService.getSubjectById(id));
        model.addAttribute("roles", Arrays.asList("ADMIN", "STUDENT", "TEACHER")); // Danh sách vai trò tĩnh
        return "editSubject";
    }

    @DeleteMapping("/subjects/{subjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "subjectId") int id) {
        this.subjectService.deleteSubject(id);
    }


}
