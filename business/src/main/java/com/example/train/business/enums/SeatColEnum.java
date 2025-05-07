package com.example.train.business.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum SeatColEnum {

    YDZ_A("A","A","1"),
    YDZ_C("C","C","1"),
    YDZ_D("D","D","1"),
    YDZ_F("F","F","1"),
    EDZ_A("A","A","2"),
    EDZ_B("B","B","2"),
    EDZ_C("C","C","2"),
    EDZ_D("D","D","2"),
    EDZ_F("F","F","2"),
    RW_S("上铺","上铺","3"),
    RW_X("下铺","下铺","3"),
    YW_S("上铺","上铺","4"),
    YW_Z("中铺","中铺","4"),
    YW_X("下铺","下铺","4");


    private String code;
    private String desc;
    /*
    对应SeatTypeEnum的code
     */
    private String type;

    SeatColEnum(String code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SeatColEnum{");
        sb.append("code='").append(code).append('\'');
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /*
    根据车厢的座位类型，筛选出所有的列
     */

    public static List<SeatColEnum> getColsByType(String seatType){
        List<SeatColEnum> colList=new ArrayList<>();
        EnumSet<SeatColEnum> seatColEnums = EnumSet.allOf(SeatColEnum.class);
        for (SeatColEnum anEnum : seatColEnums) {
            if(seatType.equals(anEnum.getType())){
                colList.add(anEnum);
            }
        }
        return colList;
    }
}
