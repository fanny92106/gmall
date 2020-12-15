package service;

import bean.SkuLsInfo;
import bean.SkuLsParams;
import bean.SkuLsResult;


public interface ListService {

    public void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    public SkuLsResult getSkuLsInfoList(SkuLsParams skuLsParams);
}
