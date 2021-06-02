package com.guli.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.gulimall.product.entity.BrandEntity;
import com.guli.gulimall.product.service.AttrGroupService;
import com.guli.gulimall.product.service.BrandService;
import com.guli.gulimall.product.service.SkuInfoService;
import com.guli.gulimall.product.vo.SkuItemVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SkuInfoService skuInfoService;

    @Test
    void test(){
//        SkuItemVo item = skuInfoService.item(18L);
//        System.out.println(item);
    }

    @Test
    void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("test","Hello:"+ UUID.randomUUID().toString());
    }


    @Test
    void contextLoads() {
        BrandEntity test = brandService.getOne(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        System.out.println(test);
    }

}
