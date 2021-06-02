package com.guli.gulimall.product.vo;


import com.guli.gulimall.product.entity.AttrEntity;
import com.guli.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import java.util.List;

@Data
public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
