package com.example.train.business.enums;

public enum RcoketMQTopicEnum {

    CONFIRM_ORDER("CONFIRM_ORDER","确认订单排队");

    private String code;
    private String desc;

    RcoketMQTopicEnum(String code,String desc){
        this.code=code;
        this.desc=desc;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    @Override
    public String toString() {
        return "RcoketMQTopicEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}'+super.toString();
    }
}



