package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.business.enums.RedisKeyPreEnum;
import com.example.train.business.mapper.cust.SkTokenMapperCust;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.example.train.business.domain.SkToken;
import com.example.train.business.domain.SkTokenExample;
import com.example.train.business.mapper.SkTokenMapper;
import com.example.train.business.req.SkTokenQueryReq;
import com.example.train.business.req.SkTokenSaveReq;
import com.example.train.business.resp.SkTokenQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenService {
    public static final Logger LOG= LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Resource
    private DailyTrainStationService dailyTrainStationService;
    @Resource
    private SkTokenMapperCust skTokenMapperCust;
    @Resource
    private RedisTemplate redisTemplate;


    public void genDaily(Date date, String trainCode){
        LOG.info("删除日期【{}】车次【{}】的令牌信息开始", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample=new SkTokenExample();
        skTokenExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now=DateTime.now();
        SkToken skToken=new SkToken();
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime( now);
        skToken.setUpdateTime( now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】的座位数：{}", trainCode, seatCount);

        long stationCount=dailyTrainStationService.countByTrainCode(date,trainCode);
        LOG.info("车次【{}】的站数：{}", trainCode, stationCount);

        int count=(int) (seatCount * stationCount); /** 3/4)*/;
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);


    }
    @Transactional
    public void save(SkTokenSaveReq req){
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);  // 新增数据
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);  // 更新数据
        }
    }
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req){
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id asc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id){
        skTokenMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取令牌
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);
        //先获取令牌锁，再校验令牌余量，防止机器人抢票
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到令牌锁了！lockKey:{}", lockKey);
        } else {
            LOG.info("很遗憾，没抢到锁！lockKey:{}", lockKey);
            return false;
        }

        String skTokenCounKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCounKey);
        if (skTokenCount != null) {
            LOG.info("缓存中有该车次令牌大闸的key：{}", skTokenCounKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCounKey, 1);
            if (count < 0L) {
                LOG.error("获取令牌失败：{}", skTokenCounKey);
                return false;
            } else {
                LOG.info("获取令牌后，令牌余数：{}", count);
                redisTemplate.expire(skTokenCounKey, 60, TimeUnit.SECONDS);
                //每获取5个令牌更新一次数据库
                if (count % 5 == 0) {
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCounKey);
            //检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isNotEmpty(tokenCountList)) {
                LOG.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }
            SkToken skToken = tokenCountList.get(0);
            if(skToken.getCount() <= 0){
                LOG.error("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            Integer count=skToken.getCount()-1;
            skToken.setCount(count);
            LOG.info("将该车次令牌大闸放入缓存中，key;{},count:{}", skTokenCounKey, count);
            redisTemplate.opsForValue().set(skTokenCounKey, count, 60, TimeUnit.SECONDS);
            skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }




        //令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存
      /*  int updateCount = skTokenMapperCust.decrease(date, trainCode);
        if(updateCount > 0){
            return true;
        }else{
            return false;
        }*/
    }
}
