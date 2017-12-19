package com.danyun.hades.common.model.redis;


import java.io.Serializable;

public class UfoCatcherRedis implements Serializable{

    private static final long serialVersionUID = -6011241820070393952L;

    //娃娃机Id
    private String UFOCatcherId;

    //在线状态
    private int onlineStatus;

    //是否空闲
    private int gameStatus;

    //上线时间
    private long loginTmDt;

    //最后一次操作时间
    private long lastUpdateTmDt;

    //最后一局游戏结束时间
    private long lastGameEndDtTm;

    //当前登录娃娃机的用户信息
    private UserIntro userIntro;


    public String getUFOCatcherId() {
        return UFOCatcherId;
    }

    public void setUFOCatcherId(String UFOCatcherId) {
        this.UFOCatcherId = UFOCatcherId;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public long getLoginTmDt() {
        return loginTmDt;
    }

    public void setLoginTmDt(long loginTmDt) {
        this.loginTmDt = loginTmDt;
    }

    public long getLastUpdateTmDt() {
        return lastUpdateTmDt;
    }

    public void setLastUpdateTmDt(long lastUpdateTmDt) {
        this.lastUpdateTmDt = lastUpdateTmDt;
    }

    public long getLastGameEndDtTm() {
        return lastGameEndDtTm;
    }

    public void setLastGameEndDtTm(long lastGameEndDtTm) {
        this.lastGameEndDtTm = lastGameEndDtTm;
    }

    public UserIntro getUserIntro() {
        return userIntro;
    }

    public void setUserIntro(UserIntro userIntro) {
        this.userIntro = userIntro;
    }
}
