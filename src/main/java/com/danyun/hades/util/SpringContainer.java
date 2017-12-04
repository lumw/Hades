package com.danyun.hades.util;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringContainer {

    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");

    private static SpringContainer instance = new SpringContainer();

    private SpringContainer() {
    }

    public static SpringContainer getInstance() {
        if (instance == null) {
            instance = new SpringContainer();
        }
        return instance;
    }


    public Object getBean(String beanId){
        return context.getBean(beanId);
    }
}
