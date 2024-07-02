package com.biluo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.biluo.config.AliyunDxConfig;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.MemberConstants;
import com.biluo.domain.Member;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.MemberMapper;
import com.biluo.service.SendService;
import com.biluo.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class SendServiceImpl implements SendService {
	private final AliyunDxConfig aliyunDxConfig;
	private final StringRedisTemplate stringRedisTemplate;
	private final MemberMapper memberMapper;

	@Override
	public void sendPhoneMsg(Map<String, Object> map) {
		// 准备配置对象
		com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
				// 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
				.setAccessKeyId(aliyunDxConfig.getAccessKeyID())
				// 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
				.setAccessKeySecret(aliyunDxConfig.getAccessKeySecret());
		// Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
		config.endpoint = aliyunDxConfig.getEndpoint();
		try {
			// 创建客户端对象
			com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);
			// 获取手机号码
			String phonenum = (String) map.get("phonenum");
			// 生成一个随机数字
			String randomNumber = RandomUtil.randomNumbers(4);
			// 将生成的随机数字存放到redis中
			stringRedisTemplate.opsForValue().set(MemberConstants.MSG_PHONE_PREFIX + phonenum, randomNumber,
					30, TimeUnit.MINUTES);
			// 创建模版参数
			String templateParam = "{\"code\":\"" + randomNumber + "\"}";
			// 创建请求参数对象
			com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
					.setPhoneNumbers(phonenum)
					.setSignName(aliyunDxConfig.getSignName())
					.setTemplateCode(aliyunDxConfig.getTemplateCode())
					.setTemplateParam(templateParam);
			// 发送请求
			client.sendSmsWithOptions(sendSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveMsgPhone(Map<String, Object> map) {
		// 获取会员输入的短信验证码
		String code = (String) map.get("code");
		// 获取会员手机号码
		String phonenum = (String) map.get("phonenum");
		// 从redis中获取当前手机号码对应验证码
		String redisCode = stringRedisTemplate.opsForValue().get(MemberConstants.MSG_PHONE_PREFIX + phonenum);
		// 判断验证码是否正确
		if (!code.equals(redisCode)) {
			throw new BusinessException(BusinessEnum.PHONE_CODE_ERROR);
		}
		// 将会员手机号码更新到会员信息中
		Member member = new Member();
		member.setUserMobile(phonenum);
		// 获取当前会员的openId
		String openId = AuthUtils.getMemberOpenId();
		return memberMapper.update(member, new LambdaUpdateWrapper<Member>()
				.eq(Member::getOpenId, openId)
		) > 0;
	}
}
