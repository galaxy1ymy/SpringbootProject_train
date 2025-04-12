package com.example.train.member.enums;

public enum PassengerTypeEnum {
    ADULT("1","成人"),
    CHILDREN("2","儿童"),
    STUDENT("3","学生");

    private String code;

    //前端用户看的
    private String desc;

    PassengerTypeEnum(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    // 根据 code 获取枚举
    public static PassengerTypeEnum getByCode(String code) {
        for (PassengerTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    // 根据 code 获取描述
    public static String getDescByCode(String code) {
        PassengerTypeEnum type = getByCode(code);
        return type != null ? type.getDesc() : "";
    }

    @Override
    public String toString() {
        return "PassengerTypeEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }


}
