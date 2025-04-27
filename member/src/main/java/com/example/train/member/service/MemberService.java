package com.example.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.util.JwtUtil;
import com.example.train.member.domain.Member;
import com.example.train.member.domain.MemberExample;
import com.example.train.member.mapper.MemberMapper;
import com.example.train.member.req.MemberLoginReq;
import com.example.train.member.req.MemberRegisterReq;
import com.example.train.member.req.MemberSendCodeReq;
import com.example.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MemberService {
    private static final Logger LOG= LoggerFactory.getLogger(MemberService.class);

    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req){
        String mobile=req.getMobile();
        Member memberDB= selectByMobile(mobile);

        if(ObjectUtil.isNotNull(memberDB)){
//            return list.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member = new Member();
        member.setId(IdUtil.getSnowflake(1,1).nextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    public void sendCode(MemberSendCodeReq req){
        String mobile=req.getMobile();
        Member memberDB= selectByMobile(mobile);

        //如果手机号不存在，则插入记录
        if(ObjectUtil.isNull(memberDB)){
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(IdUtil.getSnowflake(1,1).nextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else{
            LOG.info("手机号存在，不插入记录");
        }
        //生成验证码
        /*String code = RandomUtil.randomString(4);*/
        String code="8888";
        LOG.info("生成短信验证码：{}",code);
        //保存短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        LOG.info("保存短信记录表");
        //对接短信通道，发送短信
        LOG.info("对接短信通道");
        // 返回会员id

    }

    public MemberLoginResp login(MemberLoginReq req){
        String mobile=req.getMobile();
        String code=req.getCode();
        Member memberDB= selectByMobile(mobile);

        //如果手机号不存在，则插入记录
        if(ObjectUtil.isNull(memberDB)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        //校验短信验证码，使用数据库
        if(!"8888".equals(code)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB,MemberLoginResp.class);
        String token = JwtUtil.createToken(memberLoginResp.getId(),memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
        return memberLoginResp;

    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list= memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(list)){
            return null;
        }else{
            return list.get(0);

        }

    }


}
