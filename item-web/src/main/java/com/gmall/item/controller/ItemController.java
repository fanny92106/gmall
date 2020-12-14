package com.gmall.item.controller;


import bean.SkuInfo;
import bean.SpuSaleAttr;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.ManageService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class ItemController {

    @Reference
    ManageService manageService;

    @GetMapping("{skuId}.html")
    public String item(@PathVariable("skuId") String skuId, HttpServletRequest request){
        // basic skuInfo
        SkuInfo skuInfo = null;
        try {
            skuInfo = manageService.getSkuInfo(skuId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // spu sale attr & attr value info
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrBySpuIdCheckSku(skuId, skuInfo.getSpuId());
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("spuSaleAttrList", spuSaleAttrList);

        // get a map between saleAttrValue & skuId based on spuId, in order to page redirection based on saleAttrValue
        Map<String, String> skuValueIdsMap = manageService.getSkuValueIdsMap(skuInfo.getSpuId());
        String skuValueIdsJson = JSON.toJSONString(skuValueIdsMap);
        request.setAttribute("skuValueIdsJson", skuValueIdsJson);
        // pass as thymeleaf template variable
        return "item";
    }
}
