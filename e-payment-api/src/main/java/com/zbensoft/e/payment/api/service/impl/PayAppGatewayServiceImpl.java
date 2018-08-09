package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayAppGatewayService;
import com.zbensoft.e.payment.db.domain.PayAppGateway;
import com.zbensoft.e.payment.db.mapper.PayAppGatewayMapper;

@Service
public class PayAppGatewayServiceImpl implements PayAppGatewayService {
	@Autowired
	PayAppGatewayMapper payAppGatwayMapper;

	@Override
	public int deleteByPrimaryKey(PayAppGateway key) {
		return payAppGatwayMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(PayAppGateway record) {
		return payAppGatwayMapper.insert(record);
	}

	@Override
	public int insertSelective(PayAppGateway record) {
		return payAppGatwayMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayAppGateway record) {
		return payAppGatwayMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayAppGateway> selectPage(PayAppGateway record) {
		return payAppGatwayMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		return payAppGatwayMapper.deleteAll();
	}

	@Override
	public int count(PayAppGateway payAppGatway) {
		return payAppGatwayMapper.count(payAppGatway);
	}

	@Override
	public PayAppGateway selectByPrimaryKey(PayAppGateway payAppGatway) {
		return payAppGatwayMapper.selectByPrimaryKey(payAppGatway);
	}

	@Override
	public int updateByPrimaryKeySelective(PayAppGateway currentPayAppGatway) {
		return payAppGatwayMapper.updateByPrimaryKeySelective(currentPayAppGatway);
	}

	@Override
	public List<PayAppGateway> selectByPayAppId(String payAppId) {
		return payAppGatwayMapper.selectByPayAppId(payAppId);
	}

}
