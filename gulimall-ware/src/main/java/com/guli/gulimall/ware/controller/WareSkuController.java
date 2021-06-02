package com.guli.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.guli.common.to.WareSkuStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.guli.gulimall.ware.entity.WareSkuEntity;
import com.guli.gulimall.ware.service.WareSkuService;
import com.guli.common.utils.PageUtils;
import com.guli.common.utils.R;



/**
 * 商品库存
 *
 * @author jl
 * @email sunlightcs@gmail.com
 * @date 2021-03-21 22:10:33
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    @PostMapping("/stock")
    public R getStock(@RequestBody List<Long> skuIds){
        List<WareSkuStockTo> wareSkuStockTos = wareSkuService.getStock(skuIds);
        R ok = R.ok();
        ok.setData(wareSkuStockTos);
        return ok;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
