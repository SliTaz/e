package com.zbensoft.e.payment.api.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/statistics")
@RestController
public class StatisticsController {
	private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/getOnlineUserCount", method = RequestMethod.GET)
	public ResponseRestEntity<Long> getOnlineUserCount() {
		return new ResponseRestEntity<Long>(CommonFun.getOnlineUserCount(), HttpRestStatus.OK);
	}

}