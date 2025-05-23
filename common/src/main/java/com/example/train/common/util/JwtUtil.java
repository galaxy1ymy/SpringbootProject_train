package com.example.train.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final Logger LOG= LoggerFactory.getLogger(JwtUtil.class);

    private static final String key="train12306";

    public static String createToken(Long id,String mobile){
        DateTime now=DateTime.now();
        DateTime expTime = now.offsetNew(DateField.HOUR, 24);
        Map<String, Object> payload = new HashMap<>();
        //签发时间
        payload.put(JWTPayload.ISSUED_AT,now);
        //过期时间
        payload.put(JWTPayload.EXPIRES_AT,expTime);
        //生效时间
        payload.put(JWTPayload.NOT_BEFORE,now);
        //内容
        payload.put("id",id);
        payload.put("mobile",mobile);
        String token = JWT.create()
                .addPayloads(payload)
                .setKey(key.getBytes())
                .sign();
        LOG.info("生成JWT token:{}",token);
        return token;
    }

    public static boolean validate(String token) {
        JWT jwt = JWT.of(token).setKey(key.getBytes());
        boolean validate = jwt.validate(0);
        LOG.info("JWT token校验结果：{}", validate);
        return validate;
    }

    public static JSONObject getJSONObject(String token) {
        JWT jwt = JWT.of(token).setKey(key.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        LOG.info("根据token获取原始内容:{}", payloads);
        return payloads;
    }

    public static void main(String[] args) {
        // Create a new token
        String token = createToken(1L, "123");
        LOG.info("开始校验token");
        // Validate the newly created token
        validate(token);
        // Get payload from the token
        getJSONObject(token);
    }
}
