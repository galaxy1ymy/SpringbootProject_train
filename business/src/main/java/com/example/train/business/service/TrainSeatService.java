package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.example.train.business.domain.TrainSeat;
import com.example.train.business.domain.TrainSeatExample;
import com.example.train.business.mapper.TrainSeatMapper;
import com.example.train.business.req.TrainSeatQueryReq;
import com.example.train.business.req.TrainSeatSaveReq;
import com.example.train.business.resp.TrainSeatQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainSeatService {
    public static final Logger LOG= LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    private TrainSeatMapper trainSeatMapper;
    @Transactional
    public void save(TrainSeatSaveReq req){
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);  // 新增数据
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);  // 更新数据
        }
    }
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req){
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("id desc");
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);

        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id){
        trainSeatMapper.deleteByPrimaryKey(id);
    }
}
