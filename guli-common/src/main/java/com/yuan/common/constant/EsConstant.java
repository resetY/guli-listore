package com.yuan.common.constant;

public class EsConstant {
    public enum AttrEnum {
        PRODUCT("product");
        private String message;

        AttrEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
