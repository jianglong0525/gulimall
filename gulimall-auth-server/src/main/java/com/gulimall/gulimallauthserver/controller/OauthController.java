package com.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.guli.common.constant.LoginUserConstant;
import com.guli.common.to.SocialUserTo;
import com.guli.common.utils.HttpUtils;
import com.guli.common.utils.R;
import com.guli.common.vo.MemberLoginVo;
import com.gulimall.gulimallauthserver.feign.MemberService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
public class OauthController {
    @Autowired
    MemberService memberService;

    @GetMapping("/oauth2.0/gitee/success")
    public String socialLogin(@RequestParam("code")String code, HttpSession httpSession) throws Exception {
        HashMap<String, String> bodys = new HashMap<>();
        bodys.put("grant_type","authorization_code");
        bodys.put("code",code);
        bodys.put("client_id","d942cc189a61357b641909a59e8c3e04207c1628a2eb8fd3afdb874693d767b0");
        bodys.put("redirect_uri","http://gulimall/oauth2.0/gitee/success");
        bodys.put("client_secret","5e1ef304a2cb4122183ce9e170d292de4b2abde7e3bb18cb1cdb2cc6905eb9e7");
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<String, String>(), null, bodys);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode==200){
            String json = EntityUtils.toString(response.getEntity());
            SocialUserTo socialUserTo = JSON.parseObject(json, SocialUserTo.class);
            //判断这个用户是否为第一次登录，如果是需要自动生成注册信息
            R r = memberService.socialLogin(socialUserTo);
            if ((int)r.get("code")==0){
                //登录成功
                MemberLoginVo data = r.getData("data", new TypeReference<MemberLoginVo>() {
                });
                httpSession.setAttribute(LoginUserConstant.LOGIN_USER_SESSION_KEY,data);
                return "redirect:http://gulimall/";
            }else {
                //登录失败
                HashMap<String,String> errors = new HashMap<>();
                errors.put("code",code+"");
                errors.put("msg",(String)r.get("msg"));
                return "login";
            }
        }else {
            return "redirect:http://gulimall/login/login.html";
        }
    }
}
