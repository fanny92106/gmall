package com.gmall.manage.controller;


import bean.*;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;
import service.ListService;
import service.ManageService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
public class ManageController {

    @Reference
    ManageService manageService;

    @Reference
    ListService listService;

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

    @GetMapping("getBaseAttrInfo")
    public List<BaseAttrInfo> getBaseAttrInfo(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(catalog3Id);
        return baseAttrInfoList;
    }

    @PostMapping("saveBaseAttrInfo")
    public String saveBaseAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return "success";
    }

    @GetMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();
    }

    @GetMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }

    @PostMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return "success";
    }

    @GetMapping("spuImageList")
    public List<SpuImage> getSpuImageList(String spuId){
        return manageService.getSpuImageList(spuId);
    }

    @GetMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }

    @PostMapping("onSale")
    public String onSale(@RequestParam("skuId") String skuId){
        // copy same properties from skuInfo to skuLsInfo
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        try {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            try {
                BeanUtils.copyProperties(skuLsInfo, skuInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            listService.saveSkuLsInfo(skuLsInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
