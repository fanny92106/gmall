package com.gmall.list.controller;


import bean.BaseAttrInfo;
import bean.BaseAttrValue;
import bean.SkuLsParams;
import bean.SkuLsResult;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ListService;
import service.ManageService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Controller
public class ListController {

    @Reference
    ListService listService;

    @Reference
    ManageService manageService;

    @GetMapping("list.html")
    @ResponseBody
    public String list(SkuLsParams skuLsParams, Model model){
        SkuLsResult skuLsResult = listService.getSkuLsInfoList(skuLsParams);
        model.addAttribute("skuLsResult", skuLsResult);

        // get attr list
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);

        model.addAttribute("attrList", attrList);

        // convert input obj to url path
        String paramUrl = makeParamUrl(skuLsParams);
        model.addAttribute("paramUrl", paramUrl);

        // remove attr and attr value for all selected attr values from attrValueIdList
        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            // put selected value Id in set for quick search
            Set<String> skuLsParamsValueIdSet = new HashSet<>();
            for(String selectedValueId: skuLsParams.getValueId()){
                skuLsParamsValueIdSet.add(selectedValueId);
            }
            for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
                BaseAttrInfo baseAttrInfo =  iterator.next();
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    if(skuLsParamsValueIdSet.contains(baseAttrValue)){
                        iterator.remove(); // remove the whole baseAttrInfo if selected attr value in set
                    }
                }
            }
        }
        // return thymeleaf page
        return "list";
    }

    /**
     * convert page input obj to param url
     * @param skuLsParams
     * @return String
     */
    public String makeParamUrl(SkuLsParams skuLsParams){
        StringBuilder paramUrlBuilder = new StringBuilder();
        // append keyword
        if(skuLsParams.getKeyword()!=null){
            paramUrlBuilder.append("keyword=");
            paramUrlBuilder.append(skuLsParams.getKeyword());
        }
        // append catalog3Id
        else if(skuLsParams.getCatalog3Id()!=null){
            paramUrlBuilder.append("catalog3Id=");
            paramUrlBuilder.append(skuLsParams.getCatalog3Id());
        }
        // append valueId
        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                 String valueId = skuLsParams.getValueId()[i];
                 if(paramUrlBuilder.length()>0){
                     paramUrlBuilder.append("&");
                 }
                 paramUrlBuilder.append("valueId=");
                 paramUrlBuilder.append(valueId);
            }
        }
        return paramUrlBuilder.toString();
    }
}
