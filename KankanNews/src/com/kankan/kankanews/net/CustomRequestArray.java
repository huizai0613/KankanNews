package com.kankan.kankanews.net;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 解决 Volley中 JsonObjectRequest POST 提交不能提交参数
 * 
 * @author xiangyu.ye
 *
 */
public class CustomRequestArray extends Request<JSONArray> {

	private Listener<JSONArray> listener;
	private Map<String, String> params;

	public CustomRequestArray(String url, Map<String, String> params,
			Listener<JSONArray> reponseListener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.listener = reponseListener;
		this.params = params;
		this.setShouldCache(Boolean.FALSE);
	}

	public CustomRequestArray(int method, String url,
			Map<String, String> params, Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = reponseListener;
		this.setShouldCache(Boolean.FALSE);
		this.params = params;
	}

	protected Map<String, String> getParams()
			throws com.android.volley.AuthFailureError {
		return params;
	};

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));

			if (jsonString.equalsIgnoreCase("false")) {
				return Response.error(new ParseError(new Throwable("没有数据")));
			}
			return Response.success(new JSONArray(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONArray arg0) {
		// TODO Auto-generated method stub
		listener.onResponse(arg0);
	}

}
