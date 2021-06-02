package com.guli.common.exception;

public enum  BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验异常"),
    PHONE_CODE_TIME(10002,"获取验证码频繁，请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架错误"),
    USERNAME_EXIST_EXCEPTION(15000,"用户名已存在"),
    PHONE_EXIST_EXCEPTION(15001,"电话号码已存在"),
    USER_LOGIN_EXCEPTION(15002,"账号或密码错误"),
    USER_SOCIAL_LOGIN_EXCEPTION(15003,"第三方社交登录错误");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
