package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Note;
import com.smartStudy.pojo.User;
import com.smartStudy.repositories.NoteRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteRepositoryImpl implements NoteRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;
    private Session getCurrentSession()
    {
       return this.factoryBean.getObject().getCurrentSession();
    }
    @Override
    public Note addOrUpdate(Note no) {
        Session s = getCurrentSession();
        if(no.getId() == null)
        {
            s.persist(no);
        }
        else {
            s.merge(no);
        }
        return no;
    }

    @Override
    public List<Note> getNotes(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Note> q = b.createQuery(Note.class);
        Root root = q.from(Note.class);
        q.select(root);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String user_id = params.get("userId");
            if (user_id != null && !user_id.isEmpty()) {
                predicates.add(b.like(root.get("userId"), String.format("%%%s%%", user_id)));
            }
            String chapter_id = params.get("chapterId");
            if (chapter_id != null && !chapter_id.isEmpty()) {
                predicates.add(b.like(root.get("chapterId"), String.format("%%%s%%", chapter_id)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Note getNoteById(int id) {
        Session s = getCurrentSession();
        return s.get(Note.class,id);
    }

    @Override
    public void deleteNote(int nodeId) {
        Session s = getCurrentSession();
        Note note = this.getNoteById(nodeId);
        s.remove(note);
    }
}
