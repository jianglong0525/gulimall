package com.guli.gulimall.product.feign;

import com.guli.common.to.es.SkuEsModel;
import com.guli.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface ElasticSearchFeignService {
    @PostMapping("/elastic/save")
    public R productUpSearchSave(@RequestBody List<SkuEsModel> skuEsModel);
}
