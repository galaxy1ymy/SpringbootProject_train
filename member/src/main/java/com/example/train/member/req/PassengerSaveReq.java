package com.example.train.member.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class PassengerSaveReq {

    /**
     * id
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     private Long id;

    /**
     * 会员id
     */
     private Long memberId;

    /**
     * 姓名
     */
     @NotBlank(message = "[姓名]不能为空！")
     private String name;

    /**
     * 身份证
     */
     @NotBlank(message = "[身份证]不能为空！")
     private String idCard;

    /**
     * 旅客类型|枚举[PassengerTypeEnum]
     */
     @NotBlank(message = "[旅客类型]不能为空！")
     private String type;

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

    public Long getmemberId(){
        return memberId;
    }

    public void setMemberId(Long memberId){
        this.memberId=memberId;
    }

    public String getname(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getidCard(){
        return idCard;
    }

    public void setIdCard(String idCard){
        this.idCard=idCard;
    }

    public String gettype(){
        return type;
    }

    public void setType(String type){
        this.type=type;
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
        sb.append("memberId=").append(memberId);
        sb.append("name=").append(name);
        sb.append("idCard=").append(idCard);
        sb.append("type=").append(type);
        sb.append("createTime=").append(createTime);
        sb.append("updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}