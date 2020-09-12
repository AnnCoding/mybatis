package org.example.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.support.CronTrigger;

/**
 *
 * @author quickli
 *
 */
public class SpringQuartAnnotationCronJobSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            cleanNotExistJobInfoFromDb();
            // listAllJobInfo();
            Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(SpringQuartzCronJob.class);
            System.out.println("生成的注解任务有：" + quartzJobBeans);
            Set<String> beanNames = quartzJobBeans.keySet();
            for (String beanName : beanNames) {
                Object object = quartzJobBeans.get(beanName);
                if (object instanceof Job) {
                    Job job = (Job) object;
                    SpringQuartzCronJob quartzJobAnnotation = AnnotationUtils.findAnnotation(object.getClass(), SpringQuartzCronJob.class);
                    System.out.println("已经有的group为" + scheduler.getJobGroupNames());
                    System.out.println("需要转变成定时任务的是" + quartzJobAnnotation + object);
                    String jobName = job.getClass().getSimpleName();
                    // 直接类名当job名字
                    String groupName = job.getClass().getPackage().getName();
                    // 包名后两段当分组名
                    groupName = getLast2PackageName(groupName);
                    CronTriggerImpl trigger = new CronTriggerImpl();
                    trigger.setCronExpression(quartzJobAnnotation.cronExp());
                    trigger.setName(jobName + "_trigger");
                    trigger.setGroup(groupName);
                    JobDetailImpl jobDetail = new JobDetailImpl();
                    jobDetail.setGroup(groupName);
                    jobDetail.setName(jobName);
                    jobDetail.setJobClass(job.getClass());
                    jobDetail.setDurability(false);
                    try {
                        scheduler.scheduleJob(jobDetail, trigger);
                    } catch (SchedulerException e) {
                        // 这里可以不用打出异常，因为job已经存在了
                        // e.printStackTrace();
                    }
                    try {
                        scheduler.rescheduleJob(trigger.getKey(), trigger);
                    } catch (SchedulerException e) {
                        // e.printStackTrace();
                    }
                } else {
                    System.err.println("Job有错，不是任务" + beanName + "|");
                }

            }
            // listAllJobInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLast2PackageName(String groupName) {
        if (groupName == null || groupName.length() < 2) {
            return groupName;
        }
        int last2ndDotIndex = -1;
        int dotCount = 0;
        for (int i = groupName.length() - 1; i >= 0; i--) {
            if (groupName.charAt(i) == '.') {
                dotCount++;
            }
            if (dotCount == 2) {
                last2ndDotIndex = i;
                break;
            }
        }
        if (last2ndDotIndex >= 0) {
            return groupName.substring(last2ndDotIndex + 1);
        }
        return groupName;
    }

    /**
     * 清理数据库里存储了但是实际job class已经被删除的任务信息
     */
    private void cleanNotExistJobInfoFromDb() {
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    try {
                        Class jobClass = scheduler.getJobDetail(jobKey).getJobClass();
                        SpringQuartzCronJob springQuartzJob = (SpringQuartzCronJob) jobClass.getAnnotation(SpringQuartzCronJob.class);
                        if (springQuartzJob != null) {// 注解存在，但是cron表达式不存在，说明是临时屏蔽了任务调度
                            if (springQuartzJob.cronExp() == null || springQuartzJob.cronExp().trim().length() < 1) {
                                // 如果已经没有注解的调度计划，则也需要将定时任务清理掉
                                scheduler.deleteJob(jobKey);
                            }
                        }
                    } catch (org.quartz.JobPersistenceException e) {
                        e.printStackTrace();
                        System.out.println("异常信息为：" + e.getMessage());
                        // 一般是job对应的class文件都不存在了
                        System.out.println("需要删除的任务有" + jobKey);
                        scheduler.deleteJob(jobKey);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listAllJobInfo() {

        try {
            System.out.println("开始遍历任务listAllJobInfo");
            System.out.println("已经有的group为" + scheduler.getJobGroupNames());
            for (String groupName : scheduler.getJobGroupNames()) {

                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    try {
                        scheduler.getJobDetail(jobKey).getJobDataMap();
                        if (triggers != null && triggers.size() > 0) {
                            for (int i = 0; i < triggers.size(); i++) {
                                Date nextFireTime = triggers.get(i).getNextFireTime();
                                System.out.println("已经存在的定时任务有 " + jobName + "@" + jobGroup + " - " + nextFireTime);

                            }
                        }
                    } catch (org.quartz.JobPersistenceException e) {
                        e.printStackTrace();
                        System.out.println("异常信息为：" + e.getMessage());
                        // 一般是job对应的class文件都不存在了
                        System.out.println("需要删除的任务有" + jobKey);
                        scheduler.deleteJob(jobKey);
                        System.out.println("删除任务之后的trigger有：" + scheduler.getTriggersOfJob(jobKey));
                        if (triggers != null && triggers.size() > 0) {
                            List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>();
                            for (int i = 0; i < triggers.size(); i++) {
                                Trigger trigger = triggers.get(i);
                                triggerKeys.add(trigger.getKey());
                            }
                            System.out.println("需要取消的trigger有" + triggerKeys);
                            // scheduler.unscheduleJobs(triggerKeys);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
