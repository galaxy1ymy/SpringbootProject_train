package com.example.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberLoginReq {
    @NotBlank(message = "[手机号不能为空！]")
    @Pattern(regexp = "^1\\d{10}$",message="请输入正确的手机号!")
    public String mobile;

    @NotBlank(message = "[验证码不能为空！]")
    public String code;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberLoginReq{");
        sb.append("mobile='").append(mobile).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
