package com.danyun.hades.sockserve.service;


public interface CatcherService {

    /**
     * 发送游戏结果到服务器
     *
     *
     *
     * */
    boolean notifyResult(String operationId, String ufoCatcherId, int gameResult);
}
