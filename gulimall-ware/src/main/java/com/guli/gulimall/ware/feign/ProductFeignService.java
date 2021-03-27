package com.guli.gulimall.ware.feign;

import com.guli.common.utils.R;
import com.guli.gulimall.ware.entity.SkuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("/product/skuinfo/get/{skuId}")
    SkuInfoEntity getSkuInfo(@PathVariable("skuId") Long skuId);
}
