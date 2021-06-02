package com.gulimall.interceptor;

import com.guli.common.constant.CartConstant;
import com.guli.common.constant.LoginUserConstant;
import com.guli.common.vo.MemberLoginVo;
import com.gulimall.to.CartLoginTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal threadLocal = new ThreadLocal();//借助ThreadLocal,完成同一线程的数据快速共享

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断用户是否登录
        HttpSession session = request.getSession();
        MemberLoginVo userData = (MemberLoginVo) session.getAttribute(LoginUserConstant.LOGIN_USER_SESSION_KEY);
        CartLoginTo cartLoginTo = new CartLoginTo();
        if (userData!=null){
            //已登录,将登录的用户信息封装
            cartLoginTo.setUserData(userData);
        }
        //将临时购物车的（如果有）的cookie的user-key带上,意味着带上其临时用户时的购物车信息
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    cartLoginTo.setUserKey(cookie.getValue());
                    cartLoginTo.setIsTempUser(true);
                }
            }
        }
        threadLocal.set(cartLoginTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        CartLoginTo cartLoginTo = (CartLoginTo) threadLocal.get();
        //如果没有带有临时用户信息，那需要我们手动创建cookie保存在客户端浏览器上
        if (!cartLoginTo.getIsTempUser()){
            String uuid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,uuid);
            response.addCookie(cookie);
        }
    }
}
