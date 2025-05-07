package com.example.train.business.enums;

import java.math.BigDecimal;

public enum SeatTypeEnum {

    YDZ("1","一等座",new BigDecimal("0.4")),
    EDZ("2","二等座",new BigDecimal("0.3")),
    RW("3","软卧",new BigDecimal("0.6")),
    YW("4","硬卧",new BigDecimal("0.5"));

    private String code;
    private String desc;
    /*/
     * 基础票价 N元/公里，0.4即为0.4元/公里
     */
    private BigDecimal price;

    SeatTypeEnum(String code, String desc, BigDecimal price) {
        this.code = code;
        this.desc = desc;
        this.price = price;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SeatTypeEnum{");
        sb.append("code='").append(code).append('\'');
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", price=").append(price);
        sb.append('}');
        return sb.toString();
    }
}
