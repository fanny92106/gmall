package com.gmall.manage.service.impl;

import bean.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.manage.mapper.*;
import com.gmall.util.RedisUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import service.ManageService;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    RedisUtil redisUtil;

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

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    SaleAttrValueMapper saleAttrValueMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;


    public static final String SKUKEY_PREFIX="sku:";
    public static final String SKUKEY_INFO_SUFFIX=":info";
    public static final String SKUKEY_LOCK_SUFFIX=":lock";


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
        // loop search
//        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//        baseAttrInfo.setCatalog3Id(catalog3Id);
//        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.select(baseAttrInfo);
//
//        for (BaseAttrInfo attrInfo : baseAttrInfoList) {
//            BaseAttrValue baseAttrValue = new BaseAttrValue();
//            baseAttrValue.setAttrId(attrInfo.getId());
//            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
//            attrInfo.setAttrValueList(baseAttrValueList);
//        }

        // optimize above way by sending one sql query to search
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(catalog3Id);
        return baseAttrInfoList;
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // check if baseAttrInfo existed in db
        String baseAttrId = baseAttrInfo.getId();
        if(baseAttrId!=null && baseAttrId.length()>0){
            // update attrInfo
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else{
            // create new attrInfo
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        // delete all previous attrValue then store new onces
        Example example = new Example(BaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId", baseAttrInfo.getId());
        baseAttrValueMapper.deleteByExample(example);

        // create new attrValue
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for(BaseAttrValue baseAttrValue:attrValueList){
            String attrId = baseAttrInfo.getId();
            baseAttrValue.setAttrId(attrId);
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }

    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
        // set attrValueList in baseAttrInfo
        baseAttrInfo.setAttrValueList(baseAttrValueList);
        return baseAttrInfo;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        // save spu basic info to generate spu_ip
        spuInfoMapper.insertSelective(spuInfo);

        // image info
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setId(spuInfo.getId());
            spuImageMapper.insertSelective(spuImage);
        }

        // spu sale attr info
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setId(spuInfo.getId());

            spuSaleAttrMapper.insertSelective(spuSaleAttr);

            // sale attr value info
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                saleAttrValueMapper.insertSelective(spuSaleAttrValue);
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrBySpuId(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        // save sku basic info
        if(skuInfo.getId()==null || skuInfo.getId().length()==0){
            skuInfoMapper.insertSelective(skuInfo);
        }else{
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }
        // save sku (platform) attr
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue);

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            attrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insertSelective(attrValue);
        }
        // save sku sale attr
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.insertSelective(saleAttrValue);
        }
        // save sku image
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        skuImageMapper.delete(skuImage);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage image : skuImageList) {
            image.setSkuId(skuInfo.getId());
            skuImageMapper.insertSelective(image);
        }
    }


    public SkuInfo getSkuInfoDB(String skuId) {
        // get basic sku info
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        if(skuInfo==null){
            return null;
        }

        // get sku image list
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);

        // get sku sale attr value
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);

        // get sku attr value
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);

        // return skuInfo
        return skuInfo;
    }


    public SkuInfo getSkuInfo_redis(String skuId) {

        SkuInfo skuInfoResult=null;
        // first search redis, if not exist then search db
        Jedis jedis = redisUtil.getJedis();
        int SKU_EXPIRE_SEC=10;
        // redis structure: 1. type String 2. key sku:101:info 3. value skuInfoJson
        String skuKey=SKUKEY_PREFIX+skuId+SKUKEY_INFO_SUFFIX;
        String skuInfoJson=jedis.get(skuKey);
        if(skuInfoJson!=null){
            if(!"EMPTY".equals(skuInfoJson)){
                System.out.println(Thread.currentThread()+"cache hit!!");
                skuInfoResult = JSON.parseObject(skuInfoJson, SkuInfo.class);
            }
        }else{
            System.out.println(Thread.currentThread()+"cache miss!!");
            // search db
            // 1. redis distributed lock: setnx a. search lock b. take lock
            // define lock's structure   1. type String  2. key  sku:101:lock  3. value locked
            String token= UUID.randomUUID().toString();
            // lockKey : lock name
            String lockKey=SKUKEY_PREFIX+skuId+SKUKEY_LOCK_SUFFIX;
            // try to get lock and set timeout if the thread cannot release lock normally
            String locked = jedis.set(lockKey, token, "NX", "EX", 10);

            if("OK".equals(locked)){
                // get lock
                System.out.println(Thread.currentThread()+"get lock and hit db");
                skuInfoResult = getSkuInfoDB(skuId);

                System.out.println(Thread.currentThread()+"write to redis cache");
                String skuInfoJsonResult=null;
                if(skuInfoResult!=null){
                    skuInfoJsonResult = JSON.toJSONString(skuInfoResult);
                }else{
                    skuInfoJsonResult ="EMPTY";
                }
                jedis.setex(skuKey, SKU_EXPIRE_SEC, skuInfoJsonResult);
                System.out.println(Thread.currentThread()+"release lock");
                // delete own lock
                if(jedis.exists(lockKey)&&token.equals(jedis.get(lockKey))){  // skill cannot enforce delete own lock
                    jedis.del(lockKey);
                }
            }else{
                // didn't get lock, recursively call self
                try {
                    // wait for other thread to get the lock and update Redis cache
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // recursively self call
                getSkuInfo_redis(skuId);
            }
        }
        jedis.close();
        return skuInfoResult;
    }


    @Override
    public SkuInfo getSkuInfo(String skuId){
        SkuInfo skuInfoResult=null;
        // first search redis, if not exist then search db
        Jedis jedis = redisUtil.getJedis();
        int SKU_EXPIRE_SEC=10;
        // redis structure: 1. type String 2. key sku:101:info 3. value skuInfoJson
        String skuKey=SKUKEY_PREFIX+skuId+SKUKEY_INFO_SUFFIX;
        String skuInfoJson=jedis.get(skuKey);
        if(skuInfoJson!=null){
            if(!"EMPTY".equals(skuInfoJson)){
                System.out.println(Thread.currentThread()+"cache hit!!");
                skuInfoResult = JSON.parseObject(skuInfoJson, SkuInfo.class);
            }
        }else {
            // use redisson to ensure delete own lock (main reason to use redisson)
            Config config = new Config();
            config.useSingleServer().setAddress("redis://redis.gmall.com:6379");

            RedissonClient redissonClient = Redisson.create(config);
            String lockKey=SKUKEY_PREFIX+skuId+SKUKEY_LOCK_SUFFIX;
            RLock lock = redissonClient.getLock(lockKey);
            // set timeout
            // lock.lock(10, TimeUnit.SECONDS);
            try {
                lock.tryLock(10, 5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // if after getting lock, redis cache already updated, then get data directly from cache
            String skuInfoJsonResult=jedis.get(skuKey);
            if(skuInfoJsonResult!=null) {
                if (!"EMPTY".equals(skuInfoJsonResult)) {
                    System.out.println(Thread.currentThread() + "cache hit!!");
                    skuInfoResult = JSON.parseObject(skuInfoJson, SkuInfo.class);
                }
            }else{
                // first to get lock & hit db
                skuInfoResult = getSkuInfoDB(skuId);
                System.out.println(Thread.currentThread()+"write to redis cache");
                //  String skuInfoJsonResult=null;
                if(skuInfoResult!=null){
                    skuInfoJsonResult = JSON.toJSONString(skuInfoResult);
                }else{
                    skuInfoJsonResult ="EMPTY";
                }
                jedis.setex(skuKey, SKU_EXPIRE_SEC, skuInfoJsonResult);
            }
            // release own lock
            lock.unlock();
        }
        return skuInfoResult;
    }


    @Override
    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdCheckSku(String skuId, String spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrBySpuIdCheckSku(skuId, spuId);
    }

    @Override
    public Map<String, String> getSkuValueIdsMap(String spuId) {
        List<Map> mapList = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        Map<String, String> skuValueIdsMap = new HashMap<>();
        for(Map map: mapList){
            String skuId = String.valueOf(map.get("sku_id"));
            String valueIds = (String) map.get("value_ids");
            skuValueIdsMap.put(valueIds, skuId);
        }
        return skuValueIdsMap;
    }
}
