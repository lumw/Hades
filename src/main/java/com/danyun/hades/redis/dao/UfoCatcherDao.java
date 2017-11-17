package com.danyun.hades.redis.dao;


import com.danyun.hades.common.model.redis.UfoCatcher;

public interface UfoCatcherDao {

    void catcherRegist(UfoCatcher catcher);

    void delete(String key);

    void update(UfoCatcher user);

    String get(String catcherId);
}
