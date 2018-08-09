package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.mapper.PayGatewayMapper;

@Service
public class PayGatewayServiceImpl implements PayGatewayService {
	@Autowired
	PayGatewayMapper payGatewayMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {

		return payGatewayMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(PayGateway record) {

		return payGatewayMapper.insert(record);
	}

	@Override
	public int insertSelective(PayGateway record) {

		return payGatewayMapper.insertSelective(record);
	}

	@Override
	public PayGateway selectByPrimaryKey(String bankId) {

		return payGatewayMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(PayGateway record) {

		return payGatewayMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayGateway record) {

		return payGatewayMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayGateway> selectPage(PayGateway record) {
		return payGatewayMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		payGatewayMapper.deleteAll();
	}

	@Override
	public int count(PayGateway payGateway) {
		return payGatewayMapper.count(payGateway);
	}

	@Override
	public List<PayGateway> selectByBankBind(PayGateway payGateway) {
		return payGatewayMapper.selectByBankBind(payGateway);
	}

}
