package com.guli.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.common.utils.PageUtils;
import com.guli.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author jl
 * @email sunlightcs@gmail.com
 * @date 2021-03-21 22:10:33
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateStock(Long skuId, Long wareId, Integer skuNum);
}

