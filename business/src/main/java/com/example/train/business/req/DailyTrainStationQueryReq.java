package com.example.train.business.req;

import com.example.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainStationQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")//get请求，jsonformat是post请求
    private Date date;

    private String trainCode;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        return  "DailyTrainStationQueryReq{" +
                "date=" + date +
                ", trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }
}