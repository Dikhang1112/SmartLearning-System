package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Users;
import com.smartStudy.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private static final int PAGE_SIZE = 6;
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Users> q = b.createQuery(Users.class);
        Root root = q.from(Users.class);
        q.select(root);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("name"), String.format("%%%s%%", kw)));
            }
            String role = params.get("role");
            if (role != null && !role.isEmpty()) {
                predicates.add(b.like(root.get("role"), String.format("%%%s%%", role)));
            }
            q.where(predicates.toArray(Predicate[]::new));
            String orderBy = params.get("orderBy");
            if (orderBy != null && !orderBy.isEmpty()) {
                q.orderBy(b.asc(root.get(orderBy)));
            }
        }
        Query query = s.createQuery(q);
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PAGE_SIZE;
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }
        return query.getResultList();    }

    @Override
    public Users getUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Users.class,id);
    }

    @Override
    public Users getUserByMail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("Users.findByEmail", Users.class);
        q.setParameter("email", email);
        try {
            return (Users) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn user theo email");
        }
    }

    @Override
    public Users addOrUpdate(Users u) {
        Session s = this.factory.getObject().getCurrentSession();
        if (u.getId() == null) {
            s.persist(u);
        } else {
            s.merge(u);
        }
        return u;
    }

    @Override
    public boolean authenticate(String email, String password) {
        Users u = this.getUserByMail(email);
        return  this.passwordEncoder.matches(password,u.getPassword());
    }
}
