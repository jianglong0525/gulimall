package com.gulimall.gulimallauthserver.service.Impl;

import com.guli.common.constant.SmsConstant;
import com.guli.common.exception.BizCodeEnum;
import com.guli.common.utils.R;
import com.gulimall.gulimallauthserver.feign.ThirdPartyService;
import com.gulimall.gulimallauthserver.service.LoginService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    ThirdPartyService thirdPartyService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public R sendPhoneCode(String phone) {
        //前端的60s限制没法防止接口暴露下被恶意多次执行，后台借助redis缓存判断是否要再次发送验证码
        String s = redisTemplate.opsForValue().get(SmsConstant.SMS_CODE_CATCH_PREFIX + phone);
        if (StringUtils.isNotEmpty(s)){
            Long startTime = Long.parseLong(s.split("_")[1]);
            Long interval = System.currentTimeMillis() - startTime;
            if (interval<60000){
                return R.error(BizCodeEnum.PHONE_CODE_TIME.getCode(),BizCodeEnum.PHONE_CODE_TIME.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        //以用户电话号码为key（配上常量前缀）,value为code（加上时间戳判断时间）
        redisTemplate.opsForValue().set(SmsConstant.SMS_CODE_CATCH_PREFIX+phone,code+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);
        //TODO 短信服务收费，所以测试阶段先注释
        //thirdPartyService.sendPhoneCode(phone,code);
        return R.ok();
    }
}
