/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Subjects;
import com.smartStudy.repositories.SubjectRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author AN515-57
 */
@Repository
@Transactional
public class SubjectRepositoryImpl implements SubjectRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    @Override
    public List<Subjects> getSubjects() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Subjects", Subjects.class);
        return q.getResultList();
    }
    
    
}
