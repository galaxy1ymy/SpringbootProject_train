package com.example.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.example.train.member.domain.Passenger;
import com.example.train.member.domain.PassengerExample;
import com.example.train.member.mapper.PassengerMapper;
import com.example.train.member.req.PassengerQueryReq;
import com.example.train.member.req.PassengerSaveReq;
import com.example.train.member.resp.PassengerQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PassengerService {
    public static final Logger LOG= LoggerFactory.getLogger(PassengerService.class);

    @Resource
    private PassengerMapper passengerMapper;
    @Transactional
    public void save(PassengerSaveReq req){
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        Long loginMemberId = LoginMemberContext.getId(); // 获取当前登录用户的ID
        if (ObjectUtil.isNull(passenger.getId())) {  // 如果 id 为空，则为新增
            passenger.setMemberId(loginMemberId);  // 确保正确赋值 memberId
            passenger.setId(SnowUtil.getSnowflakeNextId());     // 生成新的唯一 id
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);  // 新增数据

        } else {  // 如果 id 不为空，则为编辑
            passenger.setMemberId(loginMemberId);
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);  // 更新数据
        }
    }
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req){
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("id desc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        Long loginMemberId = LoginMemberContext.getId();
        criteria.andMemberIdEqualTo(loginMemberId);


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);

        PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id){
        passengerMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询我的 乘客列表
     * @return
     */
    public List<PassengerQueryResp> queryMine(){
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("name asc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        criteria.andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> list = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }
}
