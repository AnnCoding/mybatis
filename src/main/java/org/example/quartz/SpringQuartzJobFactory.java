package org.example.quartz;

import org.quartz.Job;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 *
 * @author quickli
 *
 */
public class SpringQuartzJobFactory extends SpringBeanJobFactory {
    @Autowired
    private ApplicationContext ctx;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        @SuppressWarnings("unchecked")
        Job job = ctx.getBean(bundle.getJobDetail().getJobClass());
        System.out.println("找到了啊" + bundle.getJobDetail().getJobClass().getSimpleName() + "|" + job);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
        pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
        bw.setPropertyValues(pvs, true);
        return job;
    }

}
