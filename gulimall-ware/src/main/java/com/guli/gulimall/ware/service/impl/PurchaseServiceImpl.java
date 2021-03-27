package com.guli.gulimall.ware.service.impl;

import com.guli.common.constant.WareConstant;
import com.guli.gulimall.ware.entity.PurchaseDetailEntity;
import com.guli.gulimall.ware.service.PurchaseDetailService;
import com.guli.gulimall.ware.service.WareSkuService;
import com.guli.gulimall.ware.vo.DoneItemsVo;
import com.guli.gulimall.ware.vo.DoneVo;
import com.guli.gulimall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.common.utils.PageUtils;
import com.guli.common.utils.Query;

import com.guli.gulimall.ware.dao.PurchaseDao;
import com.guli.gulimall.ware.entity.PurchaseEntity;
import com.guli.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", WareConstant.PurchaseEunm.CREATED.getCode())
                        .or()
                        .eq("status",WareConstant.PurchaseEunm.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseEunm.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            Long id = purchaseEntity.getId();
            purchaseId = id;
        }
        //TODO 对应将要合并的采购项，应该判断其状态是否为0或1
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        items.stream().forEach(i -> {
            PurchaseDetailEntity detailEntity = purchaseDetailService.getBaseMapper().selectOne(new QueryWrapper<PurchaseDetailEntity>().eq("id", i));
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailEunm.ASSIGNED.getCode());
            purchaseDetailService.getBaseMapper().updateById(detailEntity);
        });
    }

    @Override
    public void receivedByPurchaseIds(List<Long> purchaseIds) {
        List<PurchaseEntity> purchaseEntities = this.getBaseMapper().selectBatchIds(purchaseIds);
        //1.将符合条件的采购单状态修改
        List<PurchaseEntity> collect = purchaseEntities.stream()
        .filter((p) -> p.getStatus() == WareConstant.PurchaseEunm.CREATED.getCode()
        || p.getStatus() == WareConstant.PurchaseEunm.ASSIGNED.getCode())
        .map(entity->{
            entity.setUpdateTime(new Date());
            entity.setStatus(WareConstant.PurchaseEunm.RECEIVED.getCode());
            this.getBaseMapper().updateById(entity);
            return entity;
        }).collect(Collectors.toList());
        //2.将对应采购单下的采购项状态修改
        collect.forEach((c)->{
            List<PurchaseDetailEntity> list = purchaseDetailService.listByPurchaseId(c.getId());
            list.forEach((w)->{
                w.setStatus(WareConstant.PurchaseDetailEunm.BUYING.getCode());
            });
            purchaseDetailService.updateBatchById(list);
        });
    }

    @Transactional
    @Override
    public void finishDone(DoneVo doneVo) {
        Long id = doneVo.getId();
        List<DoneItemsVo> items = doneVo.getItems();
        //遍历采购项，更新数据
        boolean isHasError = false;
        for ( DoneItemsVo itemVo: items){
            PurchaseDetailEntity detailEntity = purchaseDetailService.getById(itemVo.getItemId());
            Integer status = itemVo.getStatus();
            detailEntity.setStatus(status);
            //如果某个采购项存在采购异常，则记录下来，方便更新采购单的最终采购状态
            if (status==WareConstant.PurchaseDetailEunm.FAILED.getCode()){
                isHasError = true;
            }else {
                //采购成功，更新库存
                wareSkuService.updateStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
            purchaseDetailService.updateById(detailEntity);
        }
        //更新采购单
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setStatus(isHasError?WareConstant.PurchaseEunm.HAVEERROR.getCode() : WareConstant.PurchaseEunm.FINISHED.getCode());
        purchaseEntity.setId(id);
        this.updateById(purchaseEntity);
    }
}