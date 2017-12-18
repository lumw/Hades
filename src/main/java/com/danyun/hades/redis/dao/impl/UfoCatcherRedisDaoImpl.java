package com.danyun.hades.redis.dao.impl;


import com.danyun.hades.common.model.redis.UfoCatcher;
import com.danyun.hades.constant.ConstantString;
import com.danyun.hades.redis.dao.UfoCatcherDao;
import com.danyun.hades.util.SpringContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class UfoCatcherRedisDaoImpl implements UfoCatcherDao{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void catcherRegist(final UfoCatcher ufoCatcher) {

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        redisTemplate.opsForHash().put(ConstantString.Reids_Key_CatcherStatus, ufoCatcher.getUFOCatcherId(), ufoCatcher);
    }

    public void delete(String catcherId) {

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        redisTemplate.opsForHash().delete(ConstantString.Reids_Key_CatcherStatus, catcherId);
    }

    public void update(final UfoCatcher ufoCatcher) {

        String key = ufoCatcher.getUFOCatcherId();

        if (get(key) == null) {
            throw new NullPointerException("数据行不存在, key = " + key);
        }

        redisTemplate.opsForHash().put(ConstantString.Reids_Key_CatcherStatus, ufoCatcher.getUFOCatcherId(), ufoCatcher);
    }

    public UfoCatcher get(final String catcherId) {

        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");
        //redisTemplate = (RedisTemplate<String, Object>) context.getBean("redisTemplate");

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        return (UfoCatcher) redisTemplate.opsForHash().get(ConstantString.Reids_Key_CatcherStatus, catcherId);
    }
}
