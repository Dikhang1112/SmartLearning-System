package com.smartStudy.repositories;
import  com.smartStudy.pojo.Users;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    List<Users> getUsers (Map<String,String> params);
    Users getUserById(int id);
    Users getUserByMail(String email);

    Users addOrUpdate(Users u);

    boolean authenticate (String email, String password);


}
