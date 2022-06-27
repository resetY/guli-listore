package com.yuan.guli.gulimember.error;

public class UsernameExistException extends  RuntimeException {
   public UsernameExistException(){
       super("该用户名已被注册");
   }
}
