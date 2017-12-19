package com.danyun.hades.redis.dao.impl;


import com.danyun.hades.common.model.redis.UfoCatcherRedis;
import com.danyun.hades.constant.ConstantString;
import com.danyun.hades.redis.dao.UfoCatcherDao;
import com.danyun.hades.util.SpringContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class UfoCatcherRedisDaoImpl implements UfoCatcherDao{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void catcherRegist(final UfoCatcherRedis ufoCatcher) {

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        redisTemplate.opsForHash().put(ConstantString.Reids_Key_CatcherStatus, ufoCatcher.getUFOCatcherId(), ufoCatcher);
    }

    public void delete(String catcherId) {

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        redisTemplate.opsForHash().delete(ConstantString.Reids_Key_CatcherStatus, catcherId);
    }

    public void update(final UfoCatcherRedis ufoCatcher) {

        String key = ufoCatcher.getUFOCatcherId();

        if (get(key) == null) {
            throw new NullPointerException("数据行不存在, key = " + key);
        }

        redisTemplate.opsForHash().put(ConstantString.Reids_Key_CatcherStatus, ufoCatcher.getUFOCatcherId(), ufoCatcher);
    }

    public UfoCatcherRedis get(final String catcherId) {

        redisTemplate = (RedisTemplate<String, Object>) SpringContainer.getInstance().getBean("redisTemplate");
        return (UfoCatcherRedis) redisTemplate.opsForHash().get(ConstantString.Reids_Key_CatcherStatus, catcherId);
    }
}
