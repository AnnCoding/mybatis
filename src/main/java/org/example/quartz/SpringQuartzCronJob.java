package org.example.quartz;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author quickli
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope("prototype")
public @interface SpringQuartzCronJob {
    /**
     * 调度周期的cron表达式，比如每半分钟运行一次的表达式为"0/30 * * * * ?"
     *
     * @return
     */
    String cronExp();
}