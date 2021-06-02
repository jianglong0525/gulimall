package com.gulimall.to;

import com.guli.common.vo.MemberLoginVo;
import lombok.Data;

@Data
public class CartLoginTo {
    private MemberLoginVo userData;
    private String userKey;
    private Boolean isTempUser = false;
}
