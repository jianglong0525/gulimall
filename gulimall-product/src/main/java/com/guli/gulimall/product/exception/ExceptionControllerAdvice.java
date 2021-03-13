package com.guli.gulimall.product.exception;

import com.guli.common.exception.BizCodeEnum;
import com.guli.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
@Slf4j
@RestControllerAdvice(basePackages = "com.guli.gulimall.product.controller")
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R validExceptionHandler(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        HashMap<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((fieldError)->{
            map.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("date",map);
    }

    @ExceptionHandler(value = Exception.class)
    public R exceptionHandler(Exception e){
        log.error(e.toString());
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(),BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
