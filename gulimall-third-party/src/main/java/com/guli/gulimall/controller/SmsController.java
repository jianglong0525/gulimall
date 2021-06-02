package com.guli.gulimall.controller;

import com.guli.gulimall.component.SmsConponent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.guli.common.utils.R;

import java.util.UUID;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    SmsConponent smsConponent;

    @PostMapping("/sendCode")
    public R sendPhoneCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsConponent.sendPhoneCode(phone,code);
        return R.ok();
    }
}
