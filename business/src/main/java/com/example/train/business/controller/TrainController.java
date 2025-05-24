package com.example.train.business.controller;
;
import com.example.train.business.resp.TrainQueryResp;
import com.example.train.business.service.TrainSeatService;
import com.example.train.business.service.TrainService;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/train")
public class TrainController {
    @Resource
    private TrainService trainService;
    @Resource
    private TrainSeatService trainSeatService;
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

}
