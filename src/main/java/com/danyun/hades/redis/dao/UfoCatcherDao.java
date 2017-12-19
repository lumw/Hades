package com.danyun.hades.redis.dao;


import com.danyun.hades.common.model.redis.UfoCatcherRedis;

public interface UfoCatcherDao {

    void catcherRegist(UfoCatcherRedis catcher);

    void delete(String key);

    void update(UfoCatcherRedis ufoCatcher);

    UfoCatcherRedis get(String catcherId);
}
