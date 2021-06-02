package com.guli.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.guli.common.to.es.SkuEsModel;
import com.guli.search.config.GulimallSearchConfig;
import com.guli.search.constant.EsConstant;
import com.guli.search.service.ProduceUpService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProduceUpServiceImpl implements ProduceUpService {
    @Qualifier("esRestHighLevelClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean saveSearch(List<SkuEsModel> skuEsModel) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : skuEsModel) {
            String s = JSON.toJSONString(esModel);
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(EsConstant.PRODUCT_INDEX);
            indexRequest.id(esModel.getSkuId().toString());
            indexRequest.source(s,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        //批量保存
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallSearchConfig.COMMON_OPTIONS);

        //TODO 如果上架失败
        boolean failures = bulk.hasFailures();
        if (failures==true){
            List<String> collect = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());
            log.error("商品上架失败：{}",collect);
        }
        return failures;
    }
}
