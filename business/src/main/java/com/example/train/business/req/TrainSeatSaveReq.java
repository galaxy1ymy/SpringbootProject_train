package com.example.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class TrainSeatSaveReq {

    /**
     * id
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     private Long id;

    /**
     * 车次编号
     */
     @NotBlank(message = "[车次编号]不能为空！")
     private String trainCode;

    /**
     * 厢序
     */
     @NotNull(message = "[厢序]不能为空！")
     private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
     @NotBlank(message = "[排号]不能为空！")
     private String row;

    /**
     * 列号|枚举[SeatColEnum]
     */
     @NotBlank(message = "[列号]不能为空！")
     private String col;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
     @NotBlank(message = "[座位类型]不能为空！")
     private String seatType;

    /**
     * 同车厢座位号
     */
     @NotNull(message = "[同车厢座位号]不能为空！")
     private Integer carriageSeatIndex;

    /**
     * 新增时间
     */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
     private Date createTime;

    /**
     * 修改时间
     */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
     private Date updateTime;

    public Long getid(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String gettrainCode(){
        return trainCode;
    }

    public void setTrainCode(String trainCode){
        this.trainCode=trainCode;
    }

    public Integer getcarriageIndex(){
        return carriageIndex;
    }

    public void setCarriageIndex(Integer carriageIndex){
        this.carriageIndex=carriageIndex;
    }

    public String getrow(){
        return row;
    }

    public void setRow(String row){
        this.row=row;
    }

    public String getcol(){
        return col;
    }

    public void setCol(String col){
        this.col=col;
    }

    public String getseatType(){
        return seatType;
    }

    public void setSeatType(String seatType){
        this.seatType=seatType;
    }

    public Integer getcarriageSeatIndex(){
        return carriageSeatIndex;
    }

    public void setCarriageSeatIndex(Integer carriageSeatIndex){
        this.carriageSeatIndex=carriageSeatIndex;
    }

    public Date getcreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }

    public Date getupdateTime(){
        return updateTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime=updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append("id=").append(id);
        sb.append("trainCode=").append(trainCode);
        sb.append("carriageIndex=").append(carriageIndex);
        sb.append("row=").append(row);
        sb.append("col=").append(col);
        sb.append("seatType=").append(seatType);
        sb.append("carriageSeatIndex=").append(carriageSeatIndex);
        sb.append("createTime=").append(createTime);
        sb.append("updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}