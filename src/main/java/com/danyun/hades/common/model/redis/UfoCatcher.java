package com.danyun.hades.common.model.redis;


import java.io.Serializable;

public class UfoCatcher implements Serializable{

    private static final long serialVersionUID = -6011241820070393952L;

    private String UFOCatcherId;

    private String ufoCatcherStatus;

    private String lastUpdateTime;

    public UfoCatcher(String UFOCatcherId, String ufoCatcherStatus){
        this.UFOCatcherId = UFOCatcherId;
        this.ufoCatcherStatus = ufoCatcherStatus;
    }


    public String getUFOCatcherId() {
        return UFOCatcherId;
    }

    public void setUFOCatcherId(String UFOCatcherId) {
        this.UFOCatcherId = UFOCatcherId;
    }

    public String getUfoCatcherStatus() {
        return ufoCatcherStatus;
    }

    public void setUfoCatcherStatus(String ufoCatcherStatus) {
        this.ufoCatcherStatus = ufoCatcherStatus;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
