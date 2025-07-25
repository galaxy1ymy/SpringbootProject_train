package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.train.business.enums.RedisKeyPreEnum;
import com.example.train.business.mapper.cust.SkTokenMapperCust;
import com.example.train.common.context.LoginMemberContext;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


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
    public boolean validSkToken(Date date, String trainCode, Long memberId, int ticketCount) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始，票数：{}", memberId, DateUtil.formatDate(date), trainCode, ticketCount);

        // 抢个人级别令牌锁，防刷票
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("没抢到锁！lockKey:{}", lockKey);
            return false;
        }
        LOG.info("成功获取令牌锁 lockKey:{}", lockKey);

        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;

        String countStr = redisTemplate.opsForValue().get(skTokenCountKey);
        if (countStr != null) {
            int currentCount = Integer.parseInt(countStr);
            LOG.info("缓存中令牌数量：{}", currentCount);
            if (currentCount < ticketCount) {
                LOG.warn("令牌不足，当前：{}，需求：{}", currentCount, ticketCount);
                return false;
            }
            Long remain = redisTemplate.opsForValue().decrement(skTokenCountKey, ticketCount);
            if (remain < 0) {
                LOG.warn("扣减后令牌不足，剩余：{}", remain);
                return false;
            }
            LOG.info("成功扣减{}个令牌，剩余：{}", ticketCount, remain);
            redisTemplate.expire(skTokenCountKey, 3, TimeUnit.HOURS);
            if (remain % 5 == 0) {
                skTokenMapperCust.decrease(date, trainCode, 5);
            }
            return true;
        }

        LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
        // 分布式锁防止缓存击穿
        String preloadLockKey = "LOCK_PRELOAD_SK_TOKEN-" + DateUtil.formatDate(date) + "-" + trainCode;
        RLock preloadLock = redissonClient.getLock(preloadLockKey);
        boolean lockAcquired = false;
        try {
            lockAcquired = preloadLock.tryLock(3, 5, TimeUnit.SECONDS);
            if (lockAcquired) {
                // double-check
                String secondCheck = redisTemplate.opsForValue().get(skTokenCountKey);
                if (secondCheck == null) {
                    SkTokenExample example = new SkTokenExample();
                    example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
                    List<SkToken> tokenList = skTokenMapper.selectByExample(example);

                    if (CollUtil.isEmpty(tokenList)) {
                        LOG.warn("数据库中找不到该车次的令牌配置");
                        return false;
                    }

                    SkToken skToken = tokenList.get(0);
                    if (skToken.getCount() < ticketCount) {
                        LOG.warn("数据库中令牌不足，当前：{}，需求：{}", skToken.getCount(), ticketCount);
                        return false;
                    }

                    int remain = skToken.getCount() - ticketCount;
                    redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(remain), 3, TimeUnit.HOURS);
                    LOG.info("预热 Redis，key：{}，count：{}", skTokenCountKey, remain);
                    return true;
                } else {
                    // 缓存已写入，再次尝试扣减
                    int cacheCount = Integer.parseInt(secondCheck);
                    if (cacheCount < ticketCount) {
                        return false;
                    }
                    Long remain = redisTemplate.opsForValue().decrement(skTokenCountKey, ticketCount);
                    return remain >= 0;
                }
            } else {
                LOG.warn("未能获取 Redis 预热锁：{}", preloadLockKey);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
            }
        } catch (InterruptedException e) {
            LOG.error("获取 Redis 分布式锁异常", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        } finally {
            if (lockAcquired) {
                preloadLock.unlock();
            }
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
