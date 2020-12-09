package com.gmall.manage.controller;


import bean.SkuInfo;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.ManageService;

@RestController
public class SkuController {

    @Reference
    ManageService manageService;
    
    @PostMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return "success";
    }

    @GetMapping("getSkuInfo")
    public SkuInfo getSkuInfo(String skuId){
        return manageService.getSkuInfo(skuId);
    }
}
