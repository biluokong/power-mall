package com.biluo.ex.handler;

import com.biluo.constant.BusinessEnum;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

	@ExceptionHandler(BusinessException.class)
	public Result<String> businessException(BusinessException e) {
		log.error("全局异常：{}", e.getMessage());
		return Result.fail(e.getBusinessEnum());
	}

	@ExceptionHandler(RuntimeException.class)
	public Result<String> runtimeException(RuntimeException e) {
		log.error("全局异常：{}", e.getMessage());
		return Result.fail(BusinessEnum.SERVER_INNER_ERROR);
	}

	/**
	 * 权限不足
	 * <p>
	 * 这里捕捉了就无法让SpringSecurity处理，需要抛出交给SpringSecurity处理
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public Result<String> accessDeniedException(AccessDeniedException e) throws AccessDeniedException {
		log.error("全局异常：{}", e.getMessage());
		throw e;
	}
}
