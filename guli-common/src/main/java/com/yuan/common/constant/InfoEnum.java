package com.yuan.common.constant;


public enum InfoEnum {
    CURRENTPage(1,"当前页码"), //当前页
    PAGESIZE(16,"每页条数");
    private  Integer code;
    private String message;
    InfoEnum() {
    }

    InfoEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    InfoEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}



