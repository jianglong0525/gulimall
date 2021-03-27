package com.guli.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.common.utils.PageUtils;
import com.guli.gulimall.ware.entity.PurchaseDetailEntity;
import com.guli.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author jl
 * @email sunlightcs@gmail.com
 * @date 2021-03-21 22:10:34
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<PurchaseDetailEntity> listByPurchaseId(Long id);
}

