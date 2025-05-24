package com.example.train.business.controller;

import com.example.train.business.resp.StationQueryResp;
import com.example.train.business.service.StationService;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    private StationService stationService;
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
