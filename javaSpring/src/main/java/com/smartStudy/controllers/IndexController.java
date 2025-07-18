/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.controllers;

import org.springframework.ui.Model;
import com.smartStudy.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author AN515-57
 */
@Controller
@ControllerAdvice
public class IndexController {
    @Autowired
    private SubjectService subjectService;
    
    @ModelAttribute
    public void subjectResponse (Model model)
    {
         model.addAttribute("subjects", this.subjectService.getSubjects());
    }
    
    @RequestMapping("/")
        public String indexSubject(Model model) {
        model.addAttribute("subjects", this.subjectService.getSubjects());
        return "index";
    }
}
