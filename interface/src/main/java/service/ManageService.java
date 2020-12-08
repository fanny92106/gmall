package service;

import bean.*;

import java.util.List;

public interface ManageService {

    // search first catalog
    public List<BaseCatalog1> getCatalog1();

    // search second catalog based on first
    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    // search third catalog based on second
    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    // search attribute info based on third catalog
    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    // save attribute info
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    // search attribute info based on attribute id
    public BaseAttrInfo getAttrInfo(String attrId);

    // get sales attribute info
    public List<BaseSaleAttr> getBaseSaleAttrList();

    // save spu info
    public void saveSpuInfo(SpuInfo spuInfo);

    // search image list based on spuId
    public List<SpuImage> getSpuImageList(String spuId);

    // search sale attr based on spuId
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    // save sku info
    public void saveSkuInfo(SkuInfo skuInfo);

    // search sku info
    public SkuInfo getSkuInfo(String skuId);
}
