package com.danyun.hades.common.model.redis;


import java.io.Serializable;

public class UfoCatcher implements Serializable{

    private static final long serialVersionUID = -6011241820070393952L;

    //娃娃机Id
    private String UFOCatcherId;

    //在线状态
    private int onlineStatus;

    //是否空闲
    private int gameState;

    //上线时间
    private String loginTmDt;

    //最后一次操作时间
    private String lastUpdateTmDt;

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

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public String getLoginTmDt() {
        return loginTmDt;
    }

    public void setLoginTmDt(String loginTmDt) {
        this.loginTmDt = loginTmDt;
    }

    public String getLastUpdateTmDt() {
        return lastUpdateTmDt;
    }

    public void setLastUpdateTmDt(String lastUpdateTmDt) {
        this.lastUpdateTmDt = lastUpdateTmDt;
    }
}
