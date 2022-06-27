package com.yuan.guli.gulimember.error;

/**
 * 用户不存在错误
 * */
public class UserNotFoundException extends  RuntimeException {
    public UserNotFoundException(){
        super("该用户不存在，请重新输入");
    }

}
