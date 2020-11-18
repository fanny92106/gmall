package com.gmall.user.controller;

import com.gmall.user.bean.UserInfo;
import com.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("getUser")
    public UserInfo getUser(String userId) {
        UserInfo res = userService.getUserById(userId);
        return res;
    }

    @GetMapping("allUsers")
    public List<UserInfo> getAllUsers() {
        return userService.getUserInfoListAll();
    }

    @PostMapping("addUser")
    public String adUser(UserInfo userInfo) {
        userService.addUser(userInfo);
        return "success";
    }

    @PostMapping("updateUser")
    public String updateUser(UserInfo userInfo) {
        userService.updateUser(userInfo);
        return "success";
    }

    @PostMapping("updateUserByName")
    public String updateUserByName(UserInfo userInfo) {
        userService.updateUserByName(userInfo.getName(), userInfo);
        return "success";
    }

    @PostMapping("deleteUser")
    public String delUser(UserInfo userInfo) {
        userService.delUser(userInfo);
        return "success";
    }
}
