package com.danyun.hades.common.model;


public class UFOCatcherStatusCheckResponse extends BaseResponse{

    /*娃娃机状态*/
    private String catcherStatus;


    public String getCatcherStatus() {
        return catcherStatus;
    }

    public void setCatcherStatus(String catcherStatus) {
        this.catcherStatus = catcherStatus;
    }
}
