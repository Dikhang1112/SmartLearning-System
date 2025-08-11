package com.smartStudy.controllers;

import com.smartStudy.pojo.User;
import com.smartStudy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/users")
    public String userListView(Model model, @RequestParam(required = false) Map<String, String> params) {
        model.addAttribute("users", userService.getUsers(params));
        return "users";
    }

    @GetMapping("/users/add")
    public String addUserView(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Arrays.asList("ADMIN", "STUDENT", "TEACHER")); // Danh sách vai trò tĩnh
        return "editUser";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        // Nếu là update (user đã có id)
        if (user.getId() != null) {
            User currentUser = userService.getUserById(user.getId());
            // Nếu email đã thay đổi
            if (!user.getEmail().equals(currentUser.getEmail())) {
                // Kiểm tra email mới đã tồn tại ở user khác chưa
                if (userService.exitsByEmail(user.getEmail())) {
                    model.addAttribute("emailError", "Email already exists, please choose another email!");
                    model.addAttribute("user", user);
                    return "editUser";
                }
            }
            // Email giữ nguyên hoặc không trùng -> update bình thường
            userService.addUpdateUser(user);
            return "redirect:/users";
        } else { // Thêm mới
            if (userService.exitsByEmail(user.getEmail())) {
                model.addAttribute("emailError", "Email already exists, please choose another email!");
                model.addAttribute("user", user);
                return "editUser";
            }
            userService.addUpdateUser(user);
            return "redirect:/users";
        }
    }


    @GetMapping("/users/{userId}")
    public String updateUserView(Model model, @PathVariable(value = "userId") int id) {
        model.addAttribute("user", this.userService.getUserById(id));
        model.addAttribute("roles", Arrays.asList("ADMIN", "STUDENT", "TEACHER")); // Danh sách vai trò tĩnh
        return "editUser";
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "userId") int id) {
        this.userService.deleteUser(id);
    }

}
