package com.gmall.manage.mapper;

import bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {

    public List<SpuSaleAttr> getSpuSaleAttrBySpuId(String spuId);

    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdCheckSku(@Param("skuId") String skuId, @Param("skuId") String spuId);
}
