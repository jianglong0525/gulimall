package com.gulimall.gulimallauthserver.feign;

import com.guli.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-third-party")
public interface ThirdPartyService {
    @PostMapping("/sms/sendCode")
    public R sendPhoneCode(@RequestParam("phone") String phone,@RequestParam("code") String code);
}
