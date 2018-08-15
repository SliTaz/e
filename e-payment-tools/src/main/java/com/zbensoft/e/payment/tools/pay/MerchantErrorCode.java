package com.zbensoft.e.payment.tools.pay;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;

public class MerchantErrorCode {
	public static void main(String[] args) {
		String file = System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-merchant") + "\\src\\main\\resources\\static\\errorCode.json";
		JSONObject jsonObject = getAreas(file);

		Class clazz = HttpRestStatus.class;
		Object[] obs = clazz.getEnumConstants();
		for (Object ob : obs) {
			if (!ob.toString()
					.equals(HttpRestStatus.getResponseStatusCode(Integer.valueOf(ob.toString())).toString())) {
				System.err.println("============" + ob.toString());
			}
			if (Integer.valueOf(ob.toString()) < 9000) {
				continue;
			}
			HttpRestStatus httpRestStatus = HttpRestStatus.getResponseStatusCode(Integer.valueOf(ob.toString()));

			if (!jsonObject.keySet().contains(httpRestStatus.getReasonPhrase())) {

				System.err.println(httpRestStatus.getReasonPhrase());
			}
		}

	}

	public static JSONObject getAreas(String path) {
		JSONObject jsonObject = null;
		try {
			String input = FileUtils.readFileToString(new File(path), "UTF-8");
			jsonObject = JSONObject.parseObject(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}
