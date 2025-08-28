/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Student;
import com.smartStudy.statictis.SubjectStat;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;
import com.smartStudy.repositories.SubjectRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 * @author AN515-57
 */
@Repository
@Transactional
public class SubjectRepositoryImpl implements SubjectRepository {
    private static final int PAGE_SIZE = 6;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<Subject> getSubjects(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Subject> q = b.createQuery(Subject.class);
        Root<Subject> root = q.from(Subject.class);

        // Fetch teacherList và user luôn (nạp sâu)
        Fetch<Subject, Teacher> teacherFetch = root.fetch("teacherList", JoinType.LEFT);
        teacherFetch.fetch("user", JoinType.LEFT); // Nạp luôn User bên trong Teacher

        // Nếu cần join để tạo predicate, joinList (KHÔNG fetch)
        ListJoin<Subject, Teacher> teacherJoin = root.joinList("teacherList", JoinType.LEFT);
        Join<Teacher, User> userJoin = teacherJoin.join("user", JoinType.LEFT);

        q.select(root).distinct(true);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String title = params.get("title");
            if (title != null && !title.isEmpty()) {
                predicates.add(b.like(root.get("title"), "%" + title + "%"));
            }

            String teacherName = params.get("teacherName");
            if (teacherName != null && !teacherName.isEmpty()) {
                predicates.add(b.like(userJoin.get("name"), "%" + teacherName + "%"));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = s.createQuery(q);
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = page  * PAGE_SIZE;
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }

        List<Subject> subjects = query.getResultList();

        // Không cần set lại teacherList nếu đã fetch
        return subjects;
    }


    @Override
    public Subject getSubjectById(int id) {
        Session s = getCurrentSession();
        Subject subject = s.get(Subject.class, id);
        if (subject != null) {
            Hibernate.initialize(subject.getTeacherList()); // Nạp teacherList
        }
        return subject;
    }

    @Override
    public Subject addOrUpdate(Subject s) {
        Session session = getCurrentSession();
        // Lưu Subject trước (nếu mới)
        if (s.getId() == null) {
            session.persist(s);
            session.flush(); // Đảm bảo lấy được s.getId()
        } else {
            session.merge(s);
            session.flush();
        }
        // Xóa hết liên kết cũ trong bảng trung gian (nếu là update)
        String delSql = "DELETE FROM teacher_subject WHERE subject_id = :subjectId";
        session.createNativeQuery(delSql)
                .setParameter("subjectId", s.getId())
                .executeUpdate();
        // Thêm mới các liên kết teacher - subject
        if (s.getTeacherList() != null) {
            for (Teacher t : s.getTeacherList()) {
                String insSql = "INSERT INTO teacher_subject (teacher_id, subject_id) VALUES (:teacherId, :subjectId)";
                session.createNativeQuery(insSql)
                        .setParameter("teacherId", t.getUserId()) // hoặc t.getId() tùy mapping
                        .setParameter("subjectId", s.getId())
                        .executeUpdate();
            }
        }
        if (s.getStudentList() != null) {
            for (Student su : s.getStudentList()) {
                String insSql = "INSERT INTO student_subject (student_id, subject_id) VALUES (:studentId, :subjectId)";
                session.createNativeQuery(insSql)
                        .setParameter("studentId", su.getUserId()) // hoặc t.getId() tùy mapping
                        .setParameter("subjectId", s.getId())
                        .executeUpdate();
            }
        }
        return s;
    }


    @Override
    public void deleteSubject(int id) {
        Session session = getCurrentSession();
        Subject s = this.getSubjectById(id);
        //Xóa subject khỏi subjectList của từng teacher liên quan (Quan hệ many-many)
        if (s.getTeacherList() != null) {
            for (Teacher t : s.getTeacherList()) {
                t.getSubjectList().remove(s);
            }
        }

        session.remove(s);
    }


    @Override
    public Long quantityAll() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Subject> root = cq.from(Subject.class);
        cq.select(cb.count(root));
        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<SubjectStat> countBySubjectInWeek() {
        Session session = getCurrentSession();
        String hql = "SELECT new com.smartStudy.statictis.SubjectStat(s.title, COUNT(s)) " +
                "FROM Subject s " +
                "WHERE YEARWEEK(s.createdAt, 1) = YEARWEEK(CURRENT_DATE, 1) " +
                "GROUP BY s.title";
        return session.createQuery(hql, SubjectStat.class).getResultList();
    }

    @Override
    public List<SubjectStat> countBySubjectInMonth() {
        Session session = getCurrentSession();
        String hql = "SELECT new com.smartStudy.statictis.SubjectStat(s.title, COUNT(s)) " +
                "FROM Subject s " +
                "WHERE YEAR(s.createdAt) = YEAR(CURRENT_DATE) " +
                "AND MONTH(s.createdAt) = MONTH(CURRENT_DATE) " +
                "GROUP BY s.title";
        return session.createQuery(hql, SubjectStat.class).getResultList();
    }
}
