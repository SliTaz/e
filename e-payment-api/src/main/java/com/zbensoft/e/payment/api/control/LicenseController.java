package com.zbensoft.e.payment.api.control;


import java.util.HashMap;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.license.read.ReadImpl;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/license")
@RestController
public class LicenseController {

	// 查询通知，支持分页
	@ApiOperation(value = "Query lincense", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<Map<String, String>> selectInfo() {
		ReadImpl read = new ReadImpl();
		Map<String, String> info = read.getInfo();
		if(info == null){
			return new ResponseRestEntity<Map<String, String>>(new HashMap<String, String>(), HttpRestStatus.NOT_FOUND);
		}else{
			info.remove("encodekey");
			info.remove("macflag");
			info.remove("encode");
		}
		return new ResponseRestEntity<Map<String, String>>(info, HttpRestStatus.OK);
	}

}