package com.gmall.list.service.impl;


import bean.SkuLsInfo;
import bean.SkuLsParams;
import bean.SkuLsResult;
import com.alibaba.dubbo.config.annotation.Service;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import service.ListService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    @Override
    public SkuLsResult getSkuLsInfoList(SkuLsParams skuLsParams) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // check null to avoid NPE
        if(skuLsParams.getKeyword()!=null){
            // sku name search
            boolQueryBuilder.must(new MatchQueryBuilder("skuName", skuLsParams.getKeyword()));
            // highlight
            searchSourceBuilder.highlight(new HighlightBuilder().field("skuName").preTags("<span style='color:red'>").postTags("</span>"));
        }

        // category search & check null to avoid NPE
        if(skuLsParams.getCatalog3Id()!=null){
            boolQueryBuilder.filter(new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id()));
        }

        // sku attr filter & check null to avoid NPE
        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0) {
            String[] valueIds = skuLsParams.getValueId();
            for (int i = 0; i < valueIds.length; i++) {
                String valueId = valueIds[i];
                boolQueryBuilder.filter(new TermQueryBuilder("skuAttrValueList.valueId", valueId));
            }
        }
        // price
        boolQueryBuilder.filter(new RangeQueryBuilder("price").gte("3200"));
        searchSourceBuilder.query(boolQueryBuilder);

        // init row
        searchSourceBuilder.from((skuLsParams.getPageNo()-1)*skuLsParams.getPageSize());
        searchSourceBuilder.size(skuLsParams.getPageSize());

        // aggregation
        TermsBuilder aggsBuilder = AggregationBuilders.terms("groupby_value_id").field("skuAttrValueList.valueId").size(1000);
        searchSourceBuilder.aggregation(aggsBuilder);

        // sort
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        Search.Builder searchBuilder = new Search.Builder(searchSourceBuilder.toString());
        Search search = searchBuilder.addIndex("gmall_sku_info").addType("_doc").build();

        // instantiate return obj
        // encapsulate skuLsResult
        SkuLsResult skuLsResult = new SkuLsResult();
        try {
            SearchResult searchResult = jestClient.execute(search);

            // sku list
            List<SkuLsInfo> skuLsInfoList = new ArrayList<>();
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;
                skuLsInfoList.add(skuLsInfo);
            }
            skuLsResult.setSkuLsInfoList(skuLsInfoList);

            // total number
            Long total = searchResult.getTotal();
            long totalPage = (total+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
            skuLsResult.setTotalPages(totalPage);

            // aggregation part: sku attr
            List<String> attrValueIdList=new ArrayList<String>();
            List<TermsAggregation.Entry> buckets = searchResult.getAggregations().getTermsAggregation("groupby_value_id").getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add(bucket.getKey());
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skuLsResult;
    }
}
