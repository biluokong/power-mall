package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录成功的返回结果")
public class LoginResult {
	@ApiModelProperty("令牌token")
	private String accessToken;

	@ApiModelProperty("过期时间")
	private Long expiresIn;
}
