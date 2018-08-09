package com.zbensoft.e.payment.api.control;

import java.util.Calendar;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.vo.redis.RedisVo;
import com.zbensoft.e.payment.common.util.DateUtil;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/epayredis")
@RestController
public class RedisController {
	@PreAuthorize("hasRole('R_MAINTEN_MM_Q')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseRestEntity<List<RedisVo>> get(@RequestParam("key") String key) {
		List<RedisVo> list = RedisUtil.getAllRedisValue(key);

		return new ResponseRestEntity<List<RedisVo>>(list, HttpRestStatus.OK, list.size(), list.size());
	}

	@PreAuthorize("hasRole('R_MAINTEN_MM_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/set", method = RequestMethod.GET)
	public ResponseRestEntity<RedisVo> set(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("type") String type, @RequestParam("remark") String password) {
		if (!password.equals(DateUtil.convertDateToString(Calendar.getInstance().getTime(), "yyMMdd"))) {
			return new ResponseRestEntity<RedisVo>(HttpRestStatus.PASSWORD_ERROR);
		}
		if (RedisUtil.setRedisValue(key, value, type)) {
			return new ResponseRestEntity<RedisVo>(HttpRestStatus.OK);
		} else {
			return new ResponseRestEntity<RedisVo>(HttpRestStatus.UNKNOWN);
		}
	}

	@PreAuthorize("hasRole('R_MAINTEN_MM_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ResponseRestEntity<RedisVo> delete(@RequestParam("key") String key, @RequestParam("remark") String password) {
		if (!password.equals(DateUtil.convertDateToString(Calendar.getInstance().getTime(), "yyMMdd"))) {
			return new ResponseRestEntity<RedisVo>(HttpRestStatus.PASSWORD_ERROR);
		}
		RedisUtil.deleteRedisKey(key);
		return new ResponseRestEntity<RedisVo>(HttpRestStatus.OK);
	}
}