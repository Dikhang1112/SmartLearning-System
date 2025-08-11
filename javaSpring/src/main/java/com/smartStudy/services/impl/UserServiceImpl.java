package com.smartStudy.services.impl;

import com.cloudinary.utils.ObjectUtils;
import com.smartStudy.pojo.User;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public List<User> getUsers(Map<String, String> params) {
        return userRepo.getUsers(params);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.getUserById(id);
    }

    @Override
    public User getUserByMail(String email) {
        return userRepo.getUserByMail(email);
    }

    @Override
    public User addUpdateUser(User u) {
        // Lấy đối tượng hiện có nếu là cập nhật
        User existingUser = null;
        if (u.getId() != null) {
            existingUser = this.userRepo.getUserById(u.getId());
        }

        // Xử lý mật khẩu
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            u.setPassword(this.passwordEncoder.encode(u.getPassword()));
        } else if (existingUser != null) {
            // Giữ nguyên mật khẩu hiện có nếu không nhập mới
            u.setPassword(existingUser.getPassword());
        }
        // Xử lý avatar
        if (u.getFile() != null && !u.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(u.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (existingUser != null) {
            // Giữ nguyên avatar hiện có nếu không tải lên file mới
            u.setAvatar(existingUser.getAvatar());
        }

        // Xử lý createdAt và updatedAt
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());

        if (u.getId() == null) {
            // Khi add mới: đặt cả createdAt và updatedAt giống nhau
            u.setCreatedAt(currentDate);
            u.setUpdatedAt(currentDate);
        } else {
            // Khi update: chỉ cập nhật updatedAt, giữ nguyên createdAt và birthday
            u.setUpdatedAt(currentDate);
            if (existingUser != null) {
                u.setCreatedAt(existingUser.getCreatedAt());
            }
        }

        return this.userRepo.updateUser(u);
    }

    @Override
    public User addUserClient(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setName(params.get("name"));
        u.setEmail(params.get("email"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole(params.get("role"));

        if (!avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Xử lý createdAt và updatedAt
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        u.setCreatedAt(currentDate);
        u.setUpdatedAt(currentDate);
        return this.userRepo.updateUser(u);
    }

    @Override
    public void deleteUser(int id) {
        this.userRepo.deleteUser(id);
    }

    @Override
    public boolean exitsByEmail(String mail) {
        User u = userRepo.getUserByMail(mail);
        return u!=null;
    }

    /**
     * Xác thực thủ công nếu cần (ví dụ dùng cho API)
     */
    @Override
    public boolean authenticate(String email, String password) {
        User user = userRepo.getUserByMail(email);
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
        User user = userRepo.getUserByMail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy user với email: " + email);
        }
        System.out.println("Loaded user: " + user.getName() + ", Role: " + user.getRole());
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }
}
