package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.common.config.SystemConfigCodeHelp;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/systemConfigKey")
@RestController
public class SystemConfigKeyController {
	
	private static final Logger log = LoggerFactory.getLogger(SystemConfigKeyController.class);

	@ApiOperation(value = "Query systemConfigkeyMapper，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SystemConfigCodeHelp>> selectPage() {

		List<SystemConfigCodeHelp> list = new ArrayList<SystemConfigCodeHelp>();

		try {
			list = SystemConfigKey.getList(Class.forName("com.zbensoft.e.payment.common.config.SystemConfigKey"), null);
		} catch (ClassNotFoundException e) {
			log.error("",e);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SystemConfigCodeHelp>>(new ArrayList<SystemConfigCodeHelp>(), HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<SystemConfigCodeHelp>>(list, HttpRestStatus.OK);
	}

}
