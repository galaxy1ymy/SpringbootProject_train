package com.example.train.batch.req;

public class CronJobReq {
    private String group;
    private String name;
    private String description;
    private String cronException;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronException() {
        return cronException;
    }

    public void setCronException(String cronException) {
        this.cronException = cronException;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CronJobReq{");
        sb.append("group='").append(group).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", cronException='").append(cronException).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
