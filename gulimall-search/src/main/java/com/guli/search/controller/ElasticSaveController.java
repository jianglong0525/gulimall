package com.guli.search.controller;

import com.guli.common.exception.BizCodeEnum;
import com.guli.common.to.es.SkuEsModel;
import com.guli.common.utils.R;
import com.guli.search.service.ProduceUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/elastic")
public class ElasticSaveController {
    @Autowired
    ProduceUpService produceUpService;

    @PostMapping("/save")
    public R productUpSearchSave(@RequestBody List<SkuEsModel> skuEsModel){
        boolean failure = false;
        try {
            failure = produceUpService.saveSearch(skuEsModel);
        } catch (IOException e) {
            log.error("ElasticSaveController上架商品错误：{}",e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!failure){
            return R.ok();
        }else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }
}
