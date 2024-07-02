package com.biluo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtil {
	private static final ObjectMapper JSON = new ObjectMapper();

	public static String toJson(Object obj) {
		try {
			return JSON.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("json序列化失败", e);
		}
	}

	public static byte[] toBytes(Object obj) {
		try {
			return JSON.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("转换为byte数组失败", e);
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return JSON.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("json反序列化失败", e);
		}
	}

	public static <T> List<T> toList(List<String> jsons, Class<T> clazz) {
		return jsons.stream().map(json -> toObject(json, clazz)).collect(Collectors.toList());
	}

	/**
	 * 根据属性名获取json反序列化后对象的字符串属性值
	 */
	public static <T, R> String getStringValue(String json, SFunction<T, R> sf) {
		try {
			Method method = sf.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(true);
			SerializedLambda serializedLambda = (SerializedLambda) method.invoke(sf);
			String methodName = serializedLambda.getImplMethodName();
			if (!methodName.startsWith("get")) throw new Exception("非get方法的方法引用");
			String key = methodName.substring(3).toLowerCase();
			JsonNode rootNode = JSON.readTree(json);
			return rootNode.get(key).asText();
		} catch (Exception e) {
			throw new RuntimeException("json反序列化失败", e);
		}
	}

	@FunctionalInterface
	interface SFunction<T, R> extends Serializable {
		R apply(T t);
	}
}
