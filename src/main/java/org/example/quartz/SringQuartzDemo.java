package org.example.quartz;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author quickli
 *
 */
public class SringQuartzDemo {
    private static final String ACTIVE_PROFILES_PRODUCT = "product";

    private static final String APPLICTION_LOCATION = "classpath:applicationContext.xml";

    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext();
        context.getEnvironment().setActiveProfiles(ACTIVE_PROFILES_PRODUCT);
        context.setConfigLocation(APPLICTION_LOCATION);
        context.refresh();
        context.start();

    }

}
