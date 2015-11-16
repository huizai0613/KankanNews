package com.kankan.kankanews.utils;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
	private static Gson gson = new Gson();

	public static String toString(Object object) {
		return gson.toJson(object);
	}

	public static <T> T toObject(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T toObjectByType(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static Map<String, Object> toMap(String json) {
		return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
	}
}
