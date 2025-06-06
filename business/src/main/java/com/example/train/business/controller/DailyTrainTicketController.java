package com.example.train.business.controller;

import com.example.train.business.req.DailyTrainTicketQueryReq;
import com.example.train.business.resp.DailyTrainTicketQueryResp;
import com.example.train.business.service.DailyTrainTicketService;
import com.example.train.common.resp.CommonResp;
import com.example.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
