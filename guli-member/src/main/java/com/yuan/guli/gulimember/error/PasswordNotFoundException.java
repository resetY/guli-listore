package com.yuan.guli.gulimember.error;

/**
密码错误
**/
public class PasswordNotFoundException extends  RuntimeException{
    public PasswordNotFoundException() {
        super("密码输入错误，请重新输入");
    }
}
