package com.biluo.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.biluo.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class SysLogAspect {
	/**
	 * 切点表达式
	 */
	public static final String POINT_CUT = "execution(* com.biluo.controller.*.*(..))";

	@Around(POINT_CUT)
	public Object logAround(ProceedingJoinPoint point) throws Throwable {
		//获取ip地址和请求路径
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		String ip = request.getRemoteHost();
		String requestURI = request.getRequestURI();

		//获取请求参数
		Object[] args = point.getArgs();
		String argName = "";
		// 如果参数是文件
		if (ObjectUtil.isNotEmpty(args) && (args[0] instanceof MultipartFile || args[0] instanceof HttpServletResponse)) {
			argName = "file";
		} else {
			argName = JsonUtil.toJson(args);
		}

		//获取请求方法
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();
		String methodName = method.toString();

		//获取用户的操作名称
		ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
		String operationName = ObjectUtil.isNotNull(apiOperation) ? apiOperation.value() : "";

		//获取执行时长
		long beginTime = System.currentTimeMillis();
		Object result = point.proceed();
		long execTime = System.currentTimeMillis() - beginTime;

		//输出日志
		log.info("执行时间：{}，请求地址：{}，请求参数：{}，请求方法：{}，ip：{}，请求耗时：{}ms",
				new Date(), requestURI, argName, methodName, ip, execTime);
		return result;
	}
}
