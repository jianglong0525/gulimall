package com.guli.gulimall.member.exception;

public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已经被使用过!");
    }
}
