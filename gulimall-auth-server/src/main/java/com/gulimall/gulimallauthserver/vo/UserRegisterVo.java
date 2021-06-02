package com.gulimall.gulimallauthserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
@Data
public class UserRegisterVo {
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码必须位6-18位")
    private String password;

    @NotEmpty(message = "电话号码不能为空")
    @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "电话号码不对")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;

}
