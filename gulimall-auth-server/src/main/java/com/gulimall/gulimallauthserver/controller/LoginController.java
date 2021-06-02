package com.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.guli.common.constant.LoginUserConstant;
import com.guli.common.constant.SmsConstant;
import com.guli.common.utils.R;
import com.guli.common.vo.MemberLoginVo;
import com.gulimall.gulimallauthserver.feign.MemberService;
import com.gulimall.gulimallauthserver.service.LoginService;
import com.gulimall.gulimallauthserver.vo.UserLoginVo;
import com.gulimall.gulimallauthserver.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberService memberService;

    @PostMapping("/reg/sendCode")
    @ResponseBody
    public R sendPhoneCode(@RequestParam("phone") String phone){
        return loginService.sendPhoneCode(phone);
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult result, RedirectAttributes redirectAttributes,Model model){
        if (result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,(o,n)->o+'\n'+n));
            //TODO 我并不能在前端获取到这里添加的值，暂不清楚问题在哪
//            redirectAttributes.addFlashAttribute("errors",errors);
            model.addAttribute("errors",errors);
            return "reg";
        }
        //验证验证码是否正确
        String code = userRegisterVo.getCode();
        String codeKey = SmsConstant.SMS_CODE_CATCH_PREFIX + userRegisterVo.getPhone();
        Object o = redisTemplate.opsForValue().get(codeKey);
        String realCode = (String)o;
        if (StringUtils.isNotEmpty(realCode)){
            String rightCode = realCode.split("_")[0];
            if (code.equals(rightCode)){
                //验证码通过即删除保存在redis中的验证码(令牌机制)
                redisTemplate.delete(codeKey);
                //保存注册用户的信息
                R register = memberService.register(userRegisterVo);
                int errorCode = (int) register.get("code");
                if (errorCode!=0){
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg",(String) register.get("msg"));
//                    redirectAttributes.addFlashAttribute("errors",errors);
                    model.addAttribute("errors",errors);
                    return "reg";
                }else {
                    return "redirect:http://gulimall/login/login.html";
                }
            }else {
                HashMap<String, String> errors = new HashMap<>();
                errors.put("code","验证码不对");
//                redirectAttributes.addFlashAttribute("errors",errors);
                model.addAttribute("errors",errors);
                return "reg";
            }
        }else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code","验证码过期,请重新获取验证码!");
//            redirectAttributes.addFlashAttribute("errors",errors);
            model.addAttribute("errors",errors);
            return "reg";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, Model model, HttpSession httpSession){
        //远程调用用户服务，验证密码与用户名
        R login = memberService.login(vo);
        int code = (int) login.get("code");
        if (code==0){
            MemberLoginVo data = login.getData("data", new TypeReference<MemberLoginVo>() {
            });
            httpSession.setAttribute(LoginUserConstant.LOGIN_USER_SESSION_KEY,data);
            return "redirect:http://gulimall/";
        }else {
            HashMap<String,String> errors = new HashMap<>();
            errors.put("code",code+"");
            errors.put("msg",(String)login.get("msg"));
            model.addAttribute("errors",errors);
            //登录失败
            return  "login";
        }
    }

    @GetMapping("/login.html")
    public String loginIndex(HttpSession httpSession){
        Object user = httpSession.getAttribute(LoginUserConstant.LOGIN_USER_SESSION_KEY);
        if (user==null){
            //用户未登录
            return "login";
        }else {
            //用户已登录
            return "redirect:http://gulimall/";
        }
    }

}
