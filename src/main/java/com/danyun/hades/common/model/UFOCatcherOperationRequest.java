package com.danyun.hades.common.model;


public class UFOCatcherOperationRequest extends BaseRequest{

    /*操作ID, 用来记录每一局游戏*/
    private String operationId;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
