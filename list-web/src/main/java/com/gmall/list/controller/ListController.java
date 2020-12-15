package com.gmall.list.controller;


import bean.SkuLsParams;
import bean.SkuLsResult;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ListService;


@Controller
public class ListController {

    @Reference
    ListService listService;


    @GetMapping("list.html")
    @ResponseBody
    public String list(SkuLsParams skuLsParams){
        SkuLsResult skuLsResult = listService.getSkuLsInfoList(skuLsParams);
        return JSON.toJSONString(skuLsResult);
    }
}
