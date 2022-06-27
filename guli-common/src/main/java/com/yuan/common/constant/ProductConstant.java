package com.yuan.common.constant;

import lombok.Data;


@Data
public class ProductConstant {



    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTP_TYPE_SALE(0,"销售属性"),
        ATTP_TYPE_SB(2,"基本属性和销售属性二合一");
        private int code;
        private  String message;
        AttrEnum(int code,String message){
            this.code = code;
            this.message = message;
        }


        public int getCode() {
            return code;
        }

    }


    public enum SpuStatusEnum{
        NEW_SPU(0,"新建"),
        UP_SPU1(1,"上架中"),
        DOWN_SPU(2,"已下架");
        private int code;
        private  String message;
        SpuStatusEnum(int code,String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }
    }

}
