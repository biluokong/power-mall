package com.biluo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.SysLog;
import com.biluo.model.Result;
import com.biluo.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("系统日志接口管理")
@RestController
@RequestMapping("sys/log")
@RequiredArgsConstructor
public class SysLogController {
	private final SysLogService sysLogService;

	@ApiOperation("多条件分页查询系统操作日志")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('sys:log:page')")
	public Result<Page<SysLog>> loadSysLogPage(Long current, Long size,
											   @RequestParam(required = false) Long userId,
											   @RequestParam(required = false) String operation) {
		// 创建分页对象page
		Page<SysLog> page = new Page<>(current, size);
		// 多条件分页查询系统操作日志
		page = sysLogService.page(page, new LambdaQueryWrapper<SysLog>()
				// 如果查询条件eq和like同时存在的话，优先eq
				.eq(ObjectUtil.isNotNull(userId), SysLog::getUserId, userId)
				.like(StringUtils.hasText(operation), SysLog::getOperation, operation)
				.orderByDesc(SysLog::getCreateDate)
		);
		return Result.success(page);
	}
}
