package com.guli.gulimall.member.exception;

public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("该用户名已经被使用过!");
    }
}
