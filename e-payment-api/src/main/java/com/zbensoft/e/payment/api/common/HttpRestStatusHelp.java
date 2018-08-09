package com.zbensoft.e.payment.api.common;

import java.util.ArrayList;
import java.util.List;

public class HttpRestStatusHelp {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;

	private List<HttpRestStatusHelp> statusHelpList = new ArrayList<HttpRestStatusHelp>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<HttpRestStatusHelp> getStatusHelpList() {
		return statusHelpList;
	}

	public void setStatusHelpList(List<HttpRestStatusHelp> statusHelpList) {
		this.statusHelpList = statusHelpList;
	}

}
