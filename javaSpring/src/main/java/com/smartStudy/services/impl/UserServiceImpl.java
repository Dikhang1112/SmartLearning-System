package com.smartStudy.services.impl;

import com.smartStudy.pojo.Users;
import com.smartStudy.repositories.UserRepository;
import com.smartStudy.services.UserService;

import com.cloudinary.Cloudinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        return userRepo.getUsers(params);
    }

    @Override
    public Users getUserById(int id) {
        return userRepo.getUserById(id);
    }

    @Override
    public Users getUserByMail(String email) {
        return userRepo.getUserByMail(email);
    }

    @Override
    public Users addOrUpdate(Users u) {
        return this.userRepo.addOrUpdate(u);
    }

    /**
     * Xác thực thủ công nếu cần (ví dụ dùng cho API)
     */
    @Override
    public boolean authenticate(String email, String password) {
        Users user = userRepo.getUserByMail(email);
        if (user == null) {
            return false;
        }
        // So khớp password đã băm
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Spring Security callback: dùng email làm username
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.getUserByMail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy user với email: " + email);
        }
        System.out.println("Loaded user: " + user.getName() + ", Role: " + user.getRole());
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }
}
