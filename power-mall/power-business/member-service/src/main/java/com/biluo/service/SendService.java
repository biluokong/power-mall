package com.biluo.service;

import java.util.Map;

/**
 *
 */
public interface SendService {

    /**
     * 获取短信验证码
     * @param map
     */
    void sendPhoneMsg(Map<String, Object> map);

    /**
     * 绑定手机号码
     * @param map
     * @return
     */
    Boolean saveMsgPhone(Map<String, Object> map);
}
