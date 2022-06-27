package com.yuan.common.exception;

import org.omg.PortableInterceptor.USER_EXCEPTION;

/**
 * 状态码：用枚举类进行存储
 * */
public enum CodeEnume {
    UNKNON_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    ES_EXCEPTION(10002,"商品上架异常"),
    USER_EXCEPTION(15001,"用户名注册出现异常"),
    PHONE_EXCEPTION(15002,"手机注册出现异常"),
    USER_LOGIN_EXCEPTION(15003,"用户名登录异常"),
    PWS_LOGIN_EXCEPTION(15004,"登录密码出现异常"),
    NO_STOCK_EXCEPTION(15005,"商品没有库存，无法创建订单");

        private int code;
        private  String message;

    CodeEnume() {
    }

    CodeEnume(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
