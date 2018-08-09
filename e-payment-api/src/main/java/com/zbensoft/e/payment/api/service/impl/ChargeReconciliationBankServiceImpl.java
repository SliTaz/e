package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ChargeReconciliationBankService;
import com.zbensoft.e.payment.db.domain.ChargeReconciliationBank;
import com.zbensoft.e.payment.db.domain.ChargeReconciliationBankKey;
import com.zbensoft.e.payment.db.mapper.ChargeReconciliationBankMapper;


@Service
public class ChargeReconciliationBankServiceImpl implements ChargeReconciliationBankService {

	@Autowired
	ChargeReconciliationBankMapper chargeReconciliationBankMapper;
	
	@Override
	public int deleteByPrimaryKey(ChargeReconciliationBankKey key) {
		return chargeReconciliationBankMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ChargeReconciliationBank record) {
		return chargeReconciliationBankMapper.insert(record);
	}

	@Override
	public int insertSelective(ChargeReconciliationBank record) {
		return chargeReconciliationBankMapper.insertSelective(record);
	}

	@Override
	public ChargeReconciliationBank selectByPrimaryKey(ChargeReconciliationBankKey key) {
		return chargeReconciliationBankMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ChargeReconciliationBank record) {
		return chargeReconciliationBankMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ChargeReconciliationBank record) {
		return chargeReconciliationBankMapper.updateByPrimaryKey(record);
	}
	@Override
	public List<ChargeReconciliationBank> selectPage(ChargeReconciliationBank record) {
		return chargeReconciliationBankMapper.selectPage(record);
	}

	@Override
	public int count(ChargeReconciliationBank chargeReconciliationBank) {
		return chargeReconciliationBankMapper.count(chargeReconciliationBank);
	}
	
}