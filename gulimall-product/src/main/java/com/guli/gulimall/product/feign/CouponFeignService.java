package com.guli.gulimall.product.feign;

import com.guli.common.to.SkuFullReductionTo;
import com.guli.common.to.SkuLadderTo;
import com.guli.common.to.SpuBoundTo;
import com.guli.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBound(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/save")
    R saveSkuFullReduction(@RequestBody SkuFullReductionTo skuFullReductionTo);

    @PostMapping("/coupon/skuladder/save")
    R saveSkuLadder(@RequestBody SkuLadderTo skuLadderTo);
}
