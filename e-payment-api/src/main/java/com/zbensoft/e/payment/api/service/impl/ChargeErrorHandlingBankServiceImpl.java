package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ChargeErrorHandlingBankService;
import com.zbensoft.e.payment.db.domain.ChargeErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ChargeErrorHandlingBankKey;
import com.zbensoft.e.payment.db.mapper.ChargeErrorHandlingBankMapper;

@Service
public class ChargeErrorHandlingBankServiceImpl implements ChargeErrorHandlingBankService {

	@Autowired
	ChargeErrorHandlingBankMapper chargeErrorHandlingBankMapper;
	
	@Override
	public int deleteByPrimaryKey(ChargeErrorHandlingBankKey key) {
		return chargeErrorHandlingBankMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ChargeErrorHandlingBank record) {
		return chargeErrorHandlingBankMapper.insert(record);
	}

	@Override
	public int insertSelective(ChargeErrorHandlingBank record) {
		return chargeErrorHandlingBankMapper.insertSelective(record);
	}

	@Override
	public ChargeErrorHandlingBank selectByPrimaryKey(ChargeErrorHandlingBankKey key) {
		return chargeErrorHandlingBankMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ChargeErrorHandlingBank record) {
		return chargeErrorHandlingBankMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ChargeErrorHandlingBank record) {
		return chargeErrorHandlingBankMapper.updateByPrimaryKey(record);
	}

	@Override
	public void deleteByBankIdAndTime(ChargeErrorHandlingBank chargeErrorHandlingBank) {
		chargeErrorHandlingBankMapper.deleteByBankIdAndTime(chargeErrorHandlingBank);
	}
	
	@Override
	public List<ChargeErrorHandlingBank> selectPage(ChargeErrorHandlingBank record) {
		return chargeErrorHandlingBankMapper.selectPage(record);
	}

	@Override
	public int count(ChargeErrorHandlingBank chargeErrorHandlingBank) {
		return chargeErrorHandlingBankMapper.count(chargeErrorHandlingBank);
	}
	
}