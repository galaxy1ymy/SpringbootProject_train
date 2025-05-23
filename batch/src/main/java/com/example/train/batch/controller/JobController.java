package com.example.train.batch.controller;


import com.example.train.batch.req.CronJobReq;
import com.example.train.batch.resp.CronJobResp;
import com.example.train.common.resp.CommonResp;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/job")
public class JobController {

    private static Logger LOG=LoggerFactory.getLogger(JobController.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping(value = "/run")
    public CommonResp<Object> run(@RequestBody CronJobReq cronJobReq) throws SchedulerException{
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        LOG.info("手动执行定时任务开始：{}，{}",jobClassName,jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName,jobGroupName));
        return new CommonResp<>();
    }

    @RequestMapping(value = "/add")
    public CommonResp  add(@RequestBody CronJobReq cronJobReq) {
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        String cronExpression=cronJobReq.getCronExpression();
        String description=cronJobReq.getDescription();
        LOG.info("创建定时任务开始：{}，{}，{}，{}",jobClassName,jobGroupName,cronExpression,description);
        CommonResp commonResp=new CommonResp();
        try{
            //通过SchedulerFactory获取一个调度器实例
            Scheduler sched=schedulerFactoryBean.getScheduler();
            //启动调度器
            sched.start();
            //创建job信息
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassName);
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobClassName, jobGroupName)
                    .build();

            //表达式调度构建器（及任务执行时间）
            CronScheduleBuilder cronScheduleBuilder= CronScheduleBuilder.cronSchedule(cronExpression);

            //
            CronTrigger trigger= TriggerBuilder.newTrigger()
                    .withIdentity(jobClassName,jobGroupName)
                    .withDescription(description)
                    .withSchedule(cronScheduleBuilder)
                    .build();

            sched.scheduleJob(jobDetail,trigger);

        }catch (SchedulerException e){
            LOG.error("创建定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败:调度异常");
        } catch (ClassNotFoundException e) {
            LOG.error("创建定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败:任务类不存在");
        }
        LOG.info("创建定时任务结束：{}",commonResp);
        return commonResp;
    }
    @RequestMapping(value = "/pause")
    public CommonResp pause(@RequestBody CronJobReq cronJobReq) {
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        LOG.info("暂停定时任务开始：{}，{}",jobClassName,jobGroupName);
        CommonResp commonResp=new CommonResp();
        try{
            Scheduler sched=schedulerFactoryBean.getScheduler();
            sched.pauseJob(JobKey.jobKey(jobClassName,jobGroupName));
        }catch (SchedulerException e){
            LOG.error("暂停定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("暂停定时任务失败:调度异常");
        }
        LOG.info("暂停定时任务结束：{}",commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/resume")
    public CommonResp resume(@RequestBody CronJobReq cronJobReq) {
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        LOG.info("重启定时任务开始：{}，{}",jobClassName,jobGroupName);
        CommonResp commonResp=new CommonResp();
        try{
            Scheduler sched=schedulerFactoryBean.getScheduler();
            sched.resumeJob(JobKey.jobKey(jobClassName,jobGroupName));
        }catch (SchedulerException e){
            LOG.error("重启定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("重启定时任务失败:调度异常");
        }
        LOG.info("重启定时任务结束：{}",commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/reschedule")
    public CommonResp reschedule(@RequestBody CronJobReq cronJobReq) {
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        String cronExpression=cronJobReq.getCronExpression();
        String description=cronJobReq.getDescription();
        LOG.info("更新定时任务开始：{}，{}，{}，{}",jobClassName,jobGroupName,cronExpression,description);
        CommonResp commonResp=new CommonResp();
        try{
            Scheduler scheduler=schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger1.setStartTime(new Date());
            CronTrigger trigger=trigger1;
            //更新trigger
            trigger=trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //更新任务
            scheduler.rescheduleJob(triggerKey,trigger);
        }catch (SchedulerException e){
            LOG.error("更新定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("更新定时任务失败:调度异常");
        }
        LOG.info("更新定时任务结束：{}",commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/delete")
    public CommonResp delete(@RequestBody CronJobReq cronJobReq) {
        String jobClassName=cronJobReq.getName();
        String jobGroupName=cronJobReq.getGroup();
        LOG.info("删除定时任务开始：{}，{}",jobClassName,jobGroupName);
        CommonResp commonResp=new CommonResp();
        try{
            Scheduler scheduler=schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName,jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName,jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName,jobGroupName));
        }catch (SchedulerException e){
            LOG.error("删除定时任务失败",e);
            commonResp.setSuccess(false);
            commonResp.setMessage("删除定时任务失败:调度异常");
        }
        LOG.info("删除定时任务结束：{}",commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/query")
    public CommonResp query() {
        LOG.info("查询定时任务开始");
        CommonResp commonResp = new CommonResp();
        List<CronJobResp> cronJobDtoList = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for(String groupName : scheduler.getJobGroupNames()){
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
                    CronJobResp cronJobResp = new CronJobResp();
                    cronJobResp.setName(jobKey.getName());
                    cronJobResp.setGroup(jobKey.getGroup());

                    List<Trigger> triggers =(List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobResp.setCronExpression(cronTrigger.getCronExpression());
                    cronJobResp.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState  triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    cronJobResp.setState(triggerState.name());

                    cronJobDtoList.add(cronJobResp);
                }
            }
        } catch (SchedulerException e) {
            LOG.error("查询定时任务失败", e);
            commonResp.setSuccess(false);
            commonResp.setMessage("查询定时任务失败: 调度异常");
        }
        commonResp.setContent(cronJobDtoList);
        LOG.info("查询定时任务结束：{}", commonResp);
        return commonResp;
    }




}
