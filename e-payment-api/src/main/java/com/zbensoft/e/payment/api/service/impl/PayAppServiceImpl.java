package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayAppService;
import com.zbensoft.e.payment.db.domain.PayApp;
import com.zbensoft.e.payment.db.mapper.PayAppMapper;

@Service
public class PayAppServiceImpl implements PayAppService {

	@Autowired
	PayAppMapper payAppMapper;

	@Override
	public int deleteByPrimaryKey(String payAppId) {
		return payAppMapper.deleteByPrimaryKey(payAppId);
	}

	@Override
	public int insert(PayApp record) {
		return payAppMapper.insert(record);
	}

	@Override
	public int insertSelective(PayApp record) {
		return payAppMapper.insertSelective(record);
	}

	@Override
	public PayApp selectByPrimaryKey(String payAppId) {
		return payAppMapper.selectByPrimaryKey(payAppId);
	}

	@Override
	public int updateByPrimaryKeySelective(PayApp record) {
		return payAppMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayApp record) {
		return payAppMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayApp> selectPage(PayApp record) {
		return payAppMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		payAppMapper.deleteAll();
	}

	@Override
	public int count(PayApp payApp) {
		return payAppMapper.count(payApp);
	}

}
