package com.zbensoft.e.payment.api.common;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

/**
 * 
 * @author xieqiang
 *
 */
public class HttpRestStatusFactory {

	public static HttpRestStatus createStatus(List<ObjectError> list) {
		if (list != null && list.size() > 0) {
			ObjectError error = list.get(0);
			if (error != null) {
				String code = error.getCode();
				return HttpRestStatus.valueOf(code.toUpperCase());
			}
		}
		return HttpRestStatus.UNKNOWN;
	}

	public static String createStatusMessage(List<ObjectError> list) {
		if (list != null && list.size() > 0) {
			ObjectError error = list.get(0);
			if (error != null) {
				return error.getDefaultMessage();
			}
		}
		return null;
	}

}
