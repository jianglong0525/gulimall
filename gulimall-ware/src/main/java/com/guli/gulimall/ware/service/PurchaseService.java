package com.guli.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.common.utils.PageUtils;
import com.guli.gulimall.ware.entity.PurchaseEntity;
import com.guli.gulimall.ware.vo.DoneVo;
import com.guli.gulimall.ware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author jl
 * @email sunlightcs@gmail.com
 * @date 2021-03-21 22:10:34
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void merge(MergeVo mergeVo);

    void receivedByPurchaseIds(List<Long> purchaseIds);

    void finishDone(DoneVo doneVo);
}

