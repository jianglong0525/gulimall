package com.guli.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.guli.common.to.es.SkuEsModel;
import com.guli.search.config.GulimallSearchConfig;
import com.guli.search.constant.EsConstant;
import com.guli.search.service.MallSearchService;
import com.guli.search.vo.SearchParam;
import com.guli.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    RestHighLevelClient esRestHighLevelClient;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult searchResult = null;
        //构建检索请求
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            //执行检索
            SearchResponse response = esRestHighLevelClient.search(searchRequest, GulimallSearchConfig.COMMON_OPTIONS);
            //包装返回结果
            searchResult = buildSearchResult(response,searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 包装返回结果
     * @param response
     * @param queryString
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();
        SearchHit[] hits = response.getHits().getHits();
        //查询到的商品总数
        long totalHits = response.getHits().getTotalHits().value;
        searchResult.setTotal(totalHits);
        //商品信息
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        for (SearchHit hit : hits) {
            SkuEsModel skuEsModel = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
            if (StringUtils.isNotEmpty(searchParam.getKey())){
                skuEsModel.setSkuTitle(hit.getHighlightFields().get("skuTitle").getFragments()[0].string());
            }
            skuEsModels.add(skuEsModel);
        }
        searchResult.setProduct(skuEsModels);
        //总页码
        searchResult.setTotalPages((int) (totalHits % EsConstant.PRODUCT_SIZE==0?totalHits/EsConstant.PRODUCT_SIZE : totalHits/EsConstant.PRODUCT_SIZE + 1));
        //当前页码
        searchResult.setPageNum(searchParam.getPageNum());

        Aggregations aggregations = response.getAggregations();
        //品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandIdAggs = aggregations.get("brandIdAggs");
        List<? extends Terms.Bucket> buckets = brandIdAggs.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms brandImgAggs = bucket.getAggregations().get("brandImgAggs");
            brandVo.setBrandImg(brandImgAggs.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms brandNameAggs = bucket.getAggregations().get("brandNameAggs");
            brandVo.setBrandName(brandNameAggs.getBuckets().get(0).getKeyAsString());

            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);
        //分类
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogIdAggs = aggregations.get("catalogIdAggs");
        for (Terms.Bucket bucket : catalogIdAggs.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms catalogNameAggs = bucket.getAggregations().get("catalogNameAggs");
            catalogVo.setCatalogName(catalogNameAggs.getBuckets().get(0).getKeyAsString());

            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);
        //属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrs = aggregations.get("attrs");
        ParsedLongTerms attrsIdAggs = attrs.getAggregations().get("attrsIdAggs");
        for (Terms.Bucket bucket : attrsIdAggs.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms attrNameAggs = bucket.getAggregations().get("attrNameAggs");
            attrVo.setAttrName(attrNameAggs.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms attrValueAggs = bucket.getAggregations().get("attrValueAggs");
            List<String> collect = attrValueAggs.getBuckets().stream().map((item) -> {
                return item.getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(collect);

            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);
        //所有页码
        ArrayList<Integer> nav = new ArrayList<>();
        for (int i = 1; i <= searchResult.getTotalPages(); i++) {
            nav.add(i);
        }
        searchResult.setPageNavs(nav);

        //面包屑导航
        String queryString = searchParam.get_queryString();
        List<SearchResult.NavVo> navVos = new ArrayList<>();
        List<String> paramAttrs = searchParam.getAttrs();
        if (paramAttrs!=null && paramAttrs.size()>0){
            for (String attr : paramAttrs) {
                String[] s = attr.split("_");
                String attrId = s[0];
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                for (SearchResult.AttrVo attrVo : attrVos) {
                    if (attrVo.getAttrId()==Long.valueOf(attrId)){
                        navVo.setNavName(attrVo.getAttrName());
                        break;
                    }
                }
                String replace = replaceQueryString(queryString, attr,"attrs");
                navVo.setLink("http://gulimall/search/list.html?"+replace);
                navVo.setNavValue(s[1]);
                navVos.add(navVo);
            }
        }
        List<Long> brandId1 = searchParam.getBrandId();
        if (brandId1!=null){
            brandId1.stream().forEach(brandId->{
                for (SearchResult.BrandVo brand : searchResult.getBrands()) {
                    if (brand.getBrandId()==brandId){
                        SearchResult.NavVo navVo = new SearchResult.NavVo();
                        navVo.setNavName("品牌");
                        navVo.setNavValue(brand.getBrandName());
                        String replace = replaceQueryString(queryString, brand.getBrandId()+"","brandId");
                        navVo.setLink("http://gulimall/search/list.html?"+replace);
                        navVos.add(navVo);
                        break;
                    }
                }
            });
        }
        searchResult.setNavs(navVos);
        return searchResult;
    }

    private String replaceQueryString(String queryString, String val,String key) {
        String encodeVal = null;
        try {
            encodeVal = URLEncoder.encode(val, "UTF-8");
            encodeVal = encodeVal.replaceAll("\\+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = queryString.replace("&"+key+"=" + encodeVal, "");
        return replace;
    }

    /**
     * 准备检索请求DSL
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1. bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1 关键字模糊查询
        if (StringUtils.isNotEmpty(searchParam.getKey())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKey()));
        }
        //1.2 bool-filter 库存查询
        if (searchParam.getHasStock()!=null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock().equals(1)?true:false));
        }
        //1.2 bool-filter 价格区间
        if (StringUtils.isNotEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = searchParam.getSkuPrice();
            String[] price = skuPrice.split("_");
            if (price.length==1){
                skuPriceRange.gte(price[0]);
            }else if (!price[0].equals("")){
                skuPriceRange.gte(price[0]);
                skuPriceRange.lte(price[1]);
            }else {
                skuPriceRange.gte("0");
                skuPriceRange.lte(price[1]);
            }
            boolQuery.filter(skuPriceRange);
        }
        //1.2 bool-filter 品牌id
        List<Long> brandId = searchParam.getBrandId();
        if (brandId!=null && brandId.size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",brandId));
        }
        //1.2 bool-filter 分类id
        if (searchParam.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //1.2 bool-filter 属性  1_华为:小米
        List<String> attrs = searchParam.getAttrs();
        if (attrs!=null && attrs.size()>0){
            for (String attr : attrs) {
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        //查询条件封装
        sourceBuilder.query(boolQuery);

        //2. sort条件
        String sort = searchParam.getSort();
        if (StringUtils.isNotEmpty(sort)){
            String[] s = sort.split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }

        //3. 分页
        Integer pageNum = searchParam.getPageNum();
        if (pageNum!=null){
            sourceBuilder.size(EsConstant.PRODUCT_SIZE);
            sourceBuilder.from((pageNum-1)*EsConstant.PRODUCT_SIZE);
        }

        //4. 高亮
        if (StringUtils.isNotEmpty(searchParam.getKey())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        //5. 聚合分析
        //5.1 品牌聚合
        TermsAggregationBuilder brandAggs = AggregationBuilders.terms("brandIdAggs").field("brandId").size(50);
        brandAggs.subAggregation(AggregationBuilders.terms("brandNameAggs").field("brandName").size(10));
        brandAggs.subAggregation(AggregationBuilders.terms("brandImgAggs").field("brandImg").size(10));
        sourceBuilder.aggregation(brandAggs);
        //5.2 分类聚合
        TermsAggregationBuilder catalogAggs = AggregationBuilders.terms("catalogIdAggs").field("catalogId").size(10);
        catalogAggs.subAggregation(AggregationBuilders.terms("catalogNameAggs").field("catalogName").size(10));
        sourceBuilder.aggregation(catalogAggs);
        //5.3 属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrs", "attrs");
        TermsAggregationBuilder attrsIdAggs = AggregationBuilders.terms("attrsIdAggs").field("attrs.attrId").size(10);
        attrsIdAggs.subAggregation(AggregationBuilders.terms("attrNameAggs").field("attrs.attrName").size(10));
        attrsIdAggs.subAggregation(AggregationBuilders.terms("attrValueAggs").field("attrs.attrValue").size(10));
        nested.subAggregation(attrsIdAggs);
        sourceBuilder.aggregation(nested);

        System.out.println(sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},sourceBuilder);
        return searchRequest;
    }
}
