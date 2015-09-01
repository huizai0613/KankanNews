package com.kankan.kankanews.utils;

import com.google.gson.Gson;

public class JsonUtils {
	private static Gson gson = new Gson();

	public static String toString(Object object) {
		return gson.toJson(object);
	}

	public static <T> T toObject(String json, Class<T> classOfT) {
		// ObjectMapper mapper = new ObjectMapper();
		// // mapper.enableDefaultTyping();
		// mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
		// true);
		// ObjectReader reader = mapper.reader(returnType);
		// try {
		// return reader.readValue(str);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return gson.fromJson(json, classOfT);
	}
}
