package com.guli.gulimall.product.feign;

import com.guli.common.to.WareSkuStockTo;
import com.guli.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/stock")
    public R getStock(@RequestBody List<Long> skuIds);
}
