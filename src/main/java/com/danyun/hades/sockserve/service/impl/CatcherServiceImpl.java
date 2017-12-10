package com.danyun.hades.sockserve.service.impl;


import com.danyun.hades.sockserve.service.CatcherService;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

public class CatcherServiceImpl implements CatcherService{


    private RestTemplate restTemplate;

    /**
     * 发送游戏结果到服务器
     */
    public boolean notifyResult(String operationId, String ufoCatcherId, int gameResult) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operationId", operationId);
        jsonObject.put("ufoCatcherId", ufoCatcherId);
        jsonObject.put("gameResult", gameResult);

        LinkedHashMap<String, String> jsonObjectResult = (LinkedHashMap<String, String>) invokeRemoteRestService(jsonObject);

        String errorCode = jsonObjectResult.get("errorCode");

        return "0000".equals(errorCode);
    }


    public Object invokeRemoteRestService(JSONObject jsonObject) {

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toString(), headers);

        Object jsonObjectResult = restTemplate.postForObject("http://47.95.214.207:8080/poseidon/operation/notify", formEntity, Object.class);

        System.out.println("jsonObjectResult = " + jsonObjectResult.toString());

        return jsonObjectResult;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
