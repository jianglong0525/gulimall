package com.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.guli.common.utils.R;
import com.guli.common.vo.MemberLoginVo;
import com.gulimall.feign.ProductServiceFeign;
import com.gulimall.interceptor.CartInterceptor;
import com.gulimall.service.CartService;
import com.gulimall.to.CartLoginTo;
import com.gulimall.vo.CartItemVo;
import com.gulimall.vo.CartVo;
import com.gulimall.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductServiceFeign productServiceFeign;
    @Autowired
    ThreadPoolExecutor executor;
    //存放进redis的key
    private String CART_KEY_PREFIX = "gulimall:cart:";

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> redisHashOps = getRedisHashOps();
        //添加商品进购物车时，应该先判断是否购物车已经有该物品
        String json = (String) redisHashOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(json, CartItemVo.class);
        if (cartItemVo==null){
            //购物车里无此商品
            CartItemVo cartItem = new CartItemVo();
            //远程调用，查询商品的详细信息，这里应该采取异步查询的方式
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                R info = productServiceFeign.info(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setSkuId(skuId);
                cartItem.setTitle(skuInfo.getSkuTitle());

            }, executor);
            //查询sku的销售属性
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                List<String> skuAttrList = productServiceFeign.getSkuAttrList(skuId);
                cartItem.setSkuAttrValues(skuAttrList);
            }, executor);
            //等待所有异步任务完成
            CompletableFuture.allOf(future, future1).get();
            cartItemVo = cartItem;
        }else {
            cartItemVo.setCount(cartItemVo.getCount()+num);
        }
        String jsonString = JSON.toJSONString(cartItemVo);
        redisHashOps.put(skuId.toString(),jsonString);
        return cartItemVo;
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> redisHashOps = getRedisHashOps();
        String json = (String) redisHashOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(json, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        //返回购物车需要考虑临时购物车和用户登录的购物车合并问题
        //先判断是否登录
        CartLoginTo cartLoginTo = (CartLoginTo) CartInterceptor.threadLocal.get();
        String tempCartKey = CART_KEY_PREFIX + cartLoginTo.getUserKey();
        if (cartLoginTo.getUserData()==null){
            //未登陆
            List<CartItemVo> tempCart = getCartItemVos(tempCartKey);
            cartVo.setItems(tempCart);
        }else {
            String CartKey = CART_KEY_PREFIX + cartLoginTo.getUserData().getId();
            //已登录,如临时购物车有商品，需要进行合并
            List<CartItemVo> tempCartItems = getCartItemVos(tempCartKey);
            if (tempCartItems!=null && tempCartItems.size()>0){
                //临时购物车有商品,进行合并
                for (CartItemVo tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(),tempCartItem.getCount());
                }
                //合并完清空临时购物车
                clearCart(tempCartKey);
            }
            List<CartItemVo> cartItemVos = getCartItemVos(CartKey);
            cartVo.setItems(cartItemVos);
        }
        return cartVo;
    }

    @Override
    public void changeItemChecked(Long skuId, boolean checked) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(checked);
        BoundHashOperations<String, Object, Object> redisHashOps = getRedisHashOps();
        String json = JSON.toJSONString(cartItem);
        redisHashOps.put(skuId.toString(),json);
    }

    @Override
    public void changeItemNum(Long skuId, int num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> redisHashOps = getRedisHashOps();
        String json = JSON.toJSONString(cartItem);
        redisHashOps.put(skuId.toString(),json);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> redisHashOps = getRedisHashOps();
        redisHashOps.delete(skuId.toString());
    }


    public List<CartItemVo> getCartItemVos(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> cartItems = hashOps.values();
        List<CartItemVo> collect = cartItems.stream().map((cartItem) -> {
            CartItemVo cartItemVo = JSON.parseObject((String) cartItem, CartItemVo.class);
            return cartItemVo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取redis绑定好key的hash操控对象
     * @return
     */
    public BoundHashOperations<String, Object, Object> getRedisHashOps() {

        //判断用户是否登录
        CartLoginTo cartLoginTo = (CartLoginTo) CartInterceptor.threadLocal.get();
        MemberLoginVo userData = cartLoginTo.getUserData();
        String cartKey;
        if (userData==null){
            //未登录
            cartKey = CART_KEY_PREFIX+cartLoginTo.getUserKey();
        }else {
            //已经登录
            cartKey = CART_KEY_PREFIX+userData.getId();
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        return hashOps;
    }

    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }
}
