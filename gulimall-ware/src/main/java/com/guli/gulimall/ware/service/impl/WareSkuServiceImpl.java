package com.guli.gulimall.ware.service.impl;

import com.guli.common.to.WareSkuStockTo;
import com.guli.common.utils.R;
import com.guli.gulimall.ware.entity.SkuInfoEntity;
import com.guli.gulimall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.common.utils.PageUtils;
import com.guli.common.utils.Query;

import com.guli.gulimall.ware.dao.WareSkuDao;
import com.guli.gulimall.ware.entity.WareSkuEntity;
import com.guli.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuEntity entity = this.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        //如果仓库没有该商品的库存，则需要新建一个库存
        if (entity==null){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            //远程调用，查取该skuname,但远程调用服务不稳定，我们不想只因为获取name失败就整个事务回滚，所以手动catch掉
            //TODO 除了手动catch，还有什么办法让异常出现后不会回滚？
            try {
                SkuInfoEntity skuInfo = productFeignService.getSkuInfo(skuId);
                wareSkuEntity.setSkuName(skuInfo.getSkuName());
            }catch (Exception e){

            }
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            this.getBaseMapper().insert(wareSkuEntity);
        }else {
            this.getBaseMapper().updateStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<WareSkuStockTo> getStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId->{
            Long stock = this.getBaseMapper().getStock(skuId);
            WareSkuStockTo wareSkuStockTo = new WareSkuStockTo();
            wareSkuStockTo.setHasStock(stock==null?false:stock>0);
            wareSkuStockTo.setSkuId(skuId);
            return wareSkuStockTo;
        }).collect(Collectors.toList());
    }

}