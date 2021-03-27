package com.guli.gulimall.product.vo;

import com.guli.gulimall.product.entity.AttrEntity;
import com.guli.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class AttrgroupWithAttrRespVO extends AttrGroupEntity {
    private List<AttrEntity> attrs = new LinkedList<AttrEntity>();
}
