package com.yuan.guli.gulimember.vo;

import lombok.Data;

@Data
public class LoginVo {

    private String loginName; //用户名、邮箱、手机号登录皆可
    private  String passWord;
}
