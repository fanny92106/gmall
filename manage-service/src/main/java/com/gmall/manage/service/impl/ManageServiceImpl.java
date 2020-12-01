package com.gmall.manage.service.impl;

import bean.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.manage.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import service.ManageService;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1_id(catalog1Id);
        List<BaseCatalog2> baseCatalog2List = baseCatalog2Mapper.select(baseCatalog2);
        return baseCatalog2List;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2_id(catalog2Id);
        List<BaseCatalog3> baseCatalog3List = baseCatalog3Mapper.select(baseCatalog3);
        return baseCatalog3List;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.select(baseAttrInfo);
        return baseAttrInfoList;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insertSelective(baseAttrInfo);

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for(BaseAttrValue baseAttrValue:attrValueList){
            String attrId = baseAttrInfo.getId();
            baseAttrValue.setAttrId(attrId);
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }
}
