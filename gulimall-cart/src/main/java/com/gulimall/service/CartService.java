package com.gulimall.service;

import com.gulimall.vo.CartItemVo;
import com.gulimall.vo.CartVo;

import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCart() throws ExecutionException, InterruptedException;

    void changeItemChecked(Long skuId, boolean checked);

    void changeItemNum(Long skuId, int num);

    void deleteItem(Long skuId);
}
