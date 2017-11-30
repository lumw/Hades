package com.danyun.hades.util;


import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class HttpClientUtil {


    public  Object callRemoteRestService(JSONObject jsonObject) {

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");
        RestTemplate restTemplate = (RestTemplate) context.getBean("restTemplate");

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toString(), headers);
        //JSONObject jsonObjectResult = restTemplate.postForObject("http://47.95.214.207:8999/operation", formEntity, JSONObject.class);

        Object jsonObjectResult = restTemplate.postForObject("http://47.95.214.207:8999/operation", formEntity, Object.class);

        System.out.println("jsonObjectResult = " + jsonObjectResult.toString());

        return jsonObjectResult;
    }
}
