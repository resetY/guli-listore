package com.yuan.guli.gulimember.error;

//RuntimeException:运行时异常
public class PhoneExistException extends RuntimeException{
                public PhoneExistException(){
                    super("该手机号已被注册");
                }
}
