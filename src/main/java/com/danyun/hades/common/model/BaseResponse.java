package com.danyun.hades.common.model;


public class BaseResponse {

    /*娃娃机编号*/
    private String UFOCatcherId;

    /*指令编码*/
    private String actionCode;

    /*错误编码*/
    private String errorCode;

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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        errorCode = errorCode;
    }
}
