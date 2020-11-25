package com.gmall.orderweb.controller;

import bean.UserInfo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.UserService;

import java.util.List;

@RestController
public class OrderController {

    @Reference
    UserService userService;

    @GetMapping("trade")
    public List<UserInfo> tradeAll(){
        List<UserInfo> allUsers = userService.getUserInfoListAll();
        return allUsers;
    }
}
