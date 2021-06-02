package com.guli.gulimall.product.vo;

import com.guli.gulimall.product.vo.spuVo.Catalog3Vo;
import lombok.Data;

import java.util.List;
@Data
public class Catalog2Vo {
    private Long catalog1Id;
    private Long id;
    private String name;
    private List<Catalog3Vo> catalog3List;



}
