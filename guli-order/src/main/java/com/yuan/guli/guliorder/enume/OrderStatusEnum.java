package com.yuan.guli.guliorder.enume;
/**
 * 订单枚举类
 * */
public enum OrderStatusEnum {

        CREATE_NEW(0,"待付款"),
    PAYED(1,"已付款"),
    SENDED(2,"已发货"),
    RECIEVED(3,"已完成"),
    CANCLED(4,"已取消"),
    SERVICICING(5,"售后中"),
    SERVICED(6,"售后完成"),
    ;

    private Integer code;
        private  String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    OrderStatusEnum() {
    }

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
