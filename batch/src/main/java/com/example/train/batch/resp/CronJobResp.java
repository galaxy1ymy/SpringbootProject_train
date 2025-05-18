package com.example.train.batch.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CronJobResp {
    private String name;

    private String group;

    private String description;

    private String cronException;

    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preFireTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public String getCronException() {
        return cronException;
    }

    public void setCronException(String cronException) {
        this.cronException = cronException;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPreFireTime() {
        return preFireTime;
    }

    public void setPreFireTime(Date preFireTime) {
        this.preFireTime = preFireTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CronJobResp{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", cronException='").append(cronException).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", nextFireTime='").append(nextFireTime).append('\'');
        sb.append(", preFireTime='").append(preFireTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
