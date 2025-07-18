/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.services.impl;

import com.smartStudy.pojo.Subjects;
import com.smartStudy.repositories.SubjectRepository;
import com.smartStudy.services.SubjectService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author AN515-57
 */
@Service
public class SubjectServiceImpl  implements SubjectService{
    @Autowired
    private SubjectRepository subjectRepo;

    @Override
    public List<Subjects> getSubjects() {
        return this.subjectRepo.getSubjects(); 
    }   
}
