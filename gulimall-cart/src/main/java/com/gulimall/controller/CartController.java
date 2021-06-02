package com.gulimall.controller;

import com.guli.common.vo.MemberLoginVo;
import com.gulimall.interceptor.CartInterceptor;
import com.gulimall.service.CartService;
import com.gulimall.to.CartLoginTo;
import com.gulimall.vo.CartItemVo;
import com.gulimall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;


    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes ra) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        ra.addAttribute("skuId",skuId);
        return "redirect:http://gulimall/cart/addToCartSuccess.html";
    }

    /**
     * 商品添加进购物车的成功页面跳转
     * 之所以不在addToCart中直接转发到成功页面，是为了解决刷新成功页面造成多次请求重复提交问题
     * 所以需要将成功页面跳转请求单独抽出来
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId,Model model){
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }

    @GetMapping("/getCart.html")
    public String getCart(Model model) throws ExecutionException, InterruptedException {
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart",cartVo);
        return "cartList";
    }

    @GetMapping("/changeItemChecked")
    public String changeItemChecked(@RequestParam("skuId") Long skuId,@RequestParam("checked")boolean checked){
        cartService.changeItemChecked(skuId,checked);
        return "redirect:http://gulimall/cart/getCart.html";
    }

    @GetMapping("/changeItemNum")
    public String changeItemNum(@RequestParam("skuId") Long skuId,@RequestParam("num")int num){
        cartService.changeItemNum(skuId,num);
        return "redirect:http://gulimall/cart/getCart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://gulimall/cart/getCart.html";
    }
}
