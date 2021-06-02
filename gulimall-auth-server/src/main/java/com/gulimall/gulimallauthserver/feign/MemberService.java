package com.gulimall.gulimallauthserver.feign;

import com.guli.common.to.SocialUserTo;
import com.guli.common.utils.R;
import com.gulimall.gulimallauthserver.vo.UserLoginVo;
import com.gulimall.gulimallauthserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberService {
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo memberRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo memberLoginVo);

    @PostMapping("/member/member/oauth2.0/gitee/login")
    R socialLogin(@RequestBody SocialUserTo socialUserTo);
}
