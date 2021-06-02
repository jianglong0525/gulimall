package com.gulimall.gulimallauthserver.service;


import com.guli.common.utils.R;

public interface LoginService {
    R sendPhoneCode(String phone);
}
