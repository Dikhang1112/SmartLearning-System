package com.smartStudy.controllers.api;
import com.cloudinary.Cloudinary;
import com.smartStudy.pojo.User;
import com.smartStudy.services.UserService;
import com.smartStudy.untils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;
    @GetMapping("/users")
    public ResponseEntity<List<User>> userlist(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.userService.getUsers(params), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserId(@PathVariable(value = "userId") int id) {
        return new ResponseEntity<>(this.userService.getUserById(id), HttpStatus.OK);
    }
    @PostMapping(path = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@RequestParam Map<String, String> params, @RequestParam(value = "avatar") MultipartFile avatar) {
        return new ResponseEntity<>(this.userService.addUserClient(params,avatar), HttpStatus.CREATED);
    }
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "userId") int id) {
        this.userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity <?> login(@RequestBody User u)
    {
        if (this.userService.authenticate(u.getEmail(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getEmail());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }


    @RequestMapping("/auth/user")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getProfile(Principal user) {
        String principalEmail = user.getName();
        User u = userService.getUserByMail(principalEmail);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng!");
        }
        return ResponseEntity.ok(u);
    }
}
