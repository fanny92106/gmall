package com.gmall.list.service.impl;


import bean.SkuLsInfo;
import com.alibaba.dubbo.config.annotation.Service;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import service.ListService;

import java.io.IOException;


@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;

    public void saveSkuLsInfo(SkuLsInfo skuLsInfo){
        Index.Builder indexBuilder = new Index.Builder(skuLsInfo);
        indexBuilder.index("gmall_sku_info").type("_doc").id(skuLsInfo.getId());
        Index index = indexBuilder.build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
