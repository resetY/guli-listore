package com.yuan.common.constant;

public class WareConstant {
 public enum  PurchaseStatus{
        CREATES(0,"新建状态"), ASSIGN0ED(1,"已分配采购人员"),
        RECEIVE(2,"已领取"), FINISH(3,"已完成"),
        HASEHERROR(4,"异常");
        private int code;
        private  String message;
        PurchaseStatus(int code, String message) {
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

        //采购需求
    public enum  PurchaseDetailStatus{
        CREATED(0,"新建状态"), ASSIGN0ED(1,"已分配采购单"),
        BUYING(2,"正在采购"), FINISH(3,"已完成"),
        HASEHERROR(4,"采购失败");
        private int code;
        private  String message;

            PurchaseDetailStatus(int code, String message) {
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
}
