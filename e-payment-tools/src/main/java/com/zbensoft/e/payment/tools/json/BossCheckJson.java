package com.zbensoft.e.payment.tools.json;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;

public class BossCheckJson {

	private static void check() {
		String path = System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-boss") + "\\src\\main\\resources\\static\\il8n\\";
		String[] files = { "en.json", "es.json", "zh.json" };

		JSONObject jsonObject = getAreas(path + files[0]);
		for (int i = 1; i < files.length; i++) {
			JSONObject jsonObjectOther = getAreas(path + files[i]);
			for (Iterator<String> iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if (!jsonObjectOther.keySet().contains(key)) {
					System.err.println(String.format("%s key not in %s : key= %s", files[0], files[i], key));
				}
			}

			for (Iterator<String> iterator = jsonObjectOther.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if (!jsonObject.keySet().contains(key)) {
					System.err.println(String.format("%s key not in %s : key= %s", files[i], files[0], key));
				}
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

	public static void main(String[] args) {
		check();
	}

}
