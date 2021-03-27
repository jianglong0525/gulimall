package com.guli.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class DoneVo {
    private Long id;
    private List<DoneItemsVo> items;
}
