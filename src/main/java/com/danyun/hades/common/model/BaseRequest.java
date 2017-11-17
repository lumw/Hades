package com.danyun.hades.common.model;


public class BaseRequest {

    /*娃娃机编号*/
    private String UFOCatcherId;

    /*指令编码*/
    private String actionCode;

    public String getUFOCatcherId() {
        return UFOCatcherId;
    }

    public void setUFOCatcherId(String UFOCatcherId) {
        this.UFOCatcherId = UFOCatcherId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }
}
