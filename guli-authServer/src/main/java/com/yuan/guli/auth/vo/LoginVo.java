package com.yuan.guli.auth.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVo implements Serializable { //Serializable：序列化接口

    private String loginName; //用户名、邮箱、手机号登录皆可
    private  String passWord;
}
