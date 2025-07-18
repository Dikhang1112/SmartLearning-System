package com.smartStudy.services;

import com.smartStudy.pojo.Users;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<Users> getUsers (Map<String,String> params);
    Users getUserById(int id);
    Users getUserByMail(String email);
    Users addOrUpdate(Users u);
    boolean authenticate (String email, String password);
}
