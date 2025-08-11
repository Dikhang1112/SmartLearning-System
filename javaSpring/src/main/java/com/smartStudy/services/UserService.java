package com.smartStudy.services;

import com.smartStudy.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<User> getUsers (Map<String,String> params);
    User getUserById(int id);
    User getUserByMail(String email);
    User addUpdateUser(User u);

    User addUserClient(Map<String,String> params, MultipartFile avatar);
    void deleteUser (int id);
    boolean exitsByEmail(String mail);
    boolean authenticate (String email, String password);
}
