package com.danyun.hades.common.model;


public class UFOCatcherOperationResponse extends BaseResponse{

    /*娃娃机编号*/
    private String UFOCatcherId;

    /*指令编码*/
    private String actionCode;

    /*操作ID, 用来记录每一局游戏*/
    private String operationId;


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

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

}
