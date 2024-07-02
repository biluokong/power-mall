package com.biluo.controller;

import com.biluo.constant.BusinessEnum;
import com.biluo.model.Result;
import com.biluo.service.SendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 短信业务控制层
 */
@Api(tags = "短信业务接口管理")
@RequestMapping("p/sms")
@RestController
@RequiredArgsConstructor
public class SendController {
	private final SendService sendService;

	/**
	 * 获取短信验证码
	 * @param map 手机号码
	 * @return
	 */
	@ApiOperation("获取短信验证码")
	@PostMapping("send")
	public Result<String> sendPhoneMsg(@RequestBody Map<String,Object> map) {
		sendService.sendPhoneMsg(map);
		return Result.success("短信发送成功");
	}

	/**
	 * 绑定手机号码
	 * @param map 手机号码phonenum，短信验证码code
	 * @return
	 */
	@ApiOperation("绑定手机号码")
	@PostMapping("savePhone")
	public Result<String> saveMsgPhone(@RequestBody Map<String,Object> map) {
		Boolean saved = sendService.saveMsgPhone(map);
		return saved ? Result.success() : Result.fail(BusinessEnum.BINDING_PHONE_FAIL);
	}
}
