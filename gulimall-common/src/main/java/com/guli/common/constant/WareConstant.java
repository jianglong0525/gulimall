package com.guli.common.constant;

public class WareConstant {
    public enum PurchaseEunm{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),FINISHED(3,"已完成"),HAVEERROR(4,"有异常");
        private int code;
        private String msg;
        PurchaseEunm(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum PurchaseDetailEunm{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISHED(3,"已完成"),FAILED(4,"采购失败");
        private int code;
        private String msg;
        PurchaseDetailEunm(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
