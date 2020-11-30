package com.gmall.manage.controller;


import bean.BaseCatalog1;
import bean.BaseCatalog2;
import bean.BaseCatalog3;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ManageService;

import java.util.List;

@RestController
public class ManageController {

    @Reference
    ManageService manageService;

    @GetMapping("getCatalog1")
    public List<BaseCatalog1> getBaseCatalog1(){
        List<BaseCatalog1> baseCatalog1List = manageService.getCatalog1();
        return baseCatalog1List;
    }

    @GetMapping("getCatalog2")
    public List<BaseCatalog2> getBaseCatalog2(String catalog1Id){
        List<BaseCatalog2> baseCatalog2List = manageService.getCatalog2(catalog1Id);
        return baseCatalog2List;
    }

    @GetMapping("getCatalog3")
    public List<BaseCatalog3> getBaseCatalog3(String catalog2Id){
        List<BaseCatalog3> baseCatalog3List = manageService.getCatalog3(catalog2Id);
        return baseCatalog3List;
    }
}
