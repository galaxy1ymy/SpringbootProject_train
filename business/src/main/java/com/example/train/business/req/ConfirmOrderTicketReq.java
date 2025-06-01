package com.example.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmOrderTicketReq {

     @NotNull(message = "[乘客ID]不能为空！")
     private Long passangerId;

     @NotBlank(message = "[乘客票种]不能为空！")
     private String passengerType;

     @NotBlank(message = "[乘客名称]不能为空！")
     private String passengerName;

     @NotBlank(message = "[乘客的身份证]不能为空！")
     private String passangerIdCard;

     @NotBlank(message = "[座位类型]不能为空！")
     private String seatTypeCode;

    private  String seat;

    public @NotNull(message = "[乘客ID]不能为空！") Long getPassangerId() {
        return passangerId;
    }

    public void setPassangerId(@NotNull(message = "[乘客ID]不能为空！") Long passangerId) {
        this.passangerId = passangerId;
    }

    public @NotBlank(message = "[乘客票种]不能为空！") String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(@NotBlank(message = "[乘客票种]不能为空！") String passengerType) {
        this.passengerType = passengerType;
    }

    public @NotBlank(message = "[乘客名称]不能为空！") String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(@NotBlank(message = "[乘客名称]不能为空！") String passengerName) {
        this.passengerName = passengerName;
    }

    public @NotBlank(message = "[乘客的身份证]不能为空！") String getPassangerIdCard() {
        return passangerIdCard;
    }

    public void setPassangerIdCard(@NotBlank(message = "[乘客的身份证]不能为空！") String passangerIdCard) {
        this.passangerIdCard = passangerIdCard;
    }

    public @NotBlank(message = "[座位类型]不能为空！") String getSeatTypeCode() {
        return seatTypeCode;
    }

    public void setSeatTypeCode(@NotBlank(message = "[座位类型]不能为空！") String seatTypeCode) {
        this.seatTypeCode = seatTypeCode;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfirmOrderTicketReq{");
        sb.append("passangerId=").append(passangerId);
        sb.append(", passengerType=").append(passengerType);
        sb.append(", passengerName=").append(passengerName);
        sb.append(", passangerIdCard='").append(passangerIdCard).append('\'');
        sb.append(", seatTypeCode='").append(seatTypeCode).append('\'');
        sb.append(", seat='").append(seat).append('\'');
        sb.append('}');
        return sb.toString();
    }
}