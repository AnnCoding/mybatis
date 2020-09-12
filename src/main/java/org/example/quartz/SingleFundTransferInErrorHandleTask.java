package org.example.quartz;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * 单笔转入服务的差错处理定时任务
 *
 * @author quickli
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution // 不允许并发执行
@SpringQuartzCronJob(cronExp = "0/10 * * * * ?")
public class SingleFundTransferInErrorHandleTask extends QuartzJobBean {
    private static int runcc = 0;
    /**
     * 测试自动注入
     */
    @Autowired
    AppSingleTransferInDAO appSingleTransferInDAO;

    /**
     * Quartz定时任务调度
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        runcc++;
        System.out.println(runcc + "JVM Name = " + jvmName);
        JobDataMap jdm = context.getJobDetail().getJobDataMap();
        String lastRunJvm = jdm.getString("lastRunJvm");
        if (!jvmName.equals(lastRunJvm)) {
            System.err.println("任务调度从" + lastRunJvm + "抢过来成了" + jvmName);
        }
        System.out.println("上次运行的JVM是" + lastRunJvm);
        System.out.println("上次运行的时间是" + jdm.getString("startTime"));
        jdm.put("lastRunJvm", jvmName);
        jdm.put("startTime", LocalDateTime.now().toString());
        runQuartzTask();
    }

    public void runQuartzTask() {
        long startTime = System.currentTimeMillis();
        try {
            appSingleTransferInDAO.sayHello();
        } finally {
            long endTime = System.currentTimeMillis();
            long usedTime = endTime - startTime;
            System.out.println("重新调度了.runTask|usedTime|{}|ms" + usedTime);
        }

    }

}
