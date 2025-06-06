package com.example.train.business.feign;


import com.example.train.common.req.MemberTicketReq;
import com.example.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="member",url="http://localhost:8001")
public interface MemberFeign {

    @GetMapping("/member/feign/ticket/save")
    CommonResp<Object> save (@RequestBody MemberTicketReq req);
}
