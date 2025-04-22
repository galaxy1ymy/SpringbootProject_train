package com.example.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.example.train.member.domain.${Domain};
import com.example.train.member.domain.${Domain}Example;
import com.example.train.member.mapper.${Domain}Mapper;
import com.example.train.member.req.${Domain}QueryReq;
import com.example.train.member.req.${Domain}SaveReq;
import com.example.train.member.resp.${Domain}QueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ${Domain}Service {
    public static final Logger LOG= LoggerFactory.getLogger(${Domain}Service.class);

    @Resource
    private ${Domain}Mapper ${domain}Mapper;
    @Transactional
    public void save(${Domain}SaveReq req){
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        Long loginMemberId = LoginMemberContext.getId(); // 获取当前登录用户的ID
        if (ObjectUtil.isNull(${domain}.getId())) {  // 如果 id 为空，则为新增
            ${domain}.setMemberId(loginMemberId);  // 确保正确赋值 memberId
            ${domain}.setId(SnowUtil.getSnowflakeNextId());     // 生成新的唯一 id
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});  // 新增数据

        } else {  // 如果 id 不为空，则为编辑
            ${domain}.setMemberId(loginMemberId);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});  // 更新数据
        }
    }
    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req){
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.setOrderByClause("id desc");
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        Long loginMemberId = LoginMemberContext.getId();
        criteria.andMemberIdEqualTo(loginMemberId);


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);

        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id){
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
