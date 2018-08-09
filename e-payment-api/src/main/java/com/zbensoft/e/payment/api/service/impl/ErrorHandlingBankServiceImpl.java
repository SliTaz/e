package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankService;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankKey;
import com.zbensoft.e.payment.db.domain.ReconciliationBank;
import com.zbensoft.e.payment.db.mapper.ErrorHandlingBankMapper;

@Service
public class ErrorHandlingBankServiceImpl implements ErrorHandlingBankService {

	@Autowired
	ErrorHandlingBankMapper errorHandlingBankMapper;

	@Override
	public int deleteByPrimaryKey(ErrorHandlingBankKey key) {
		return errorHandlingBankMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ErrorHandlingBank record) {
		return errorHandlingBankMapper.insert(record);
	}

	@Override
	public int insertSelective(ErrorHandlingBank record) {
		return errorHandlingBankMapper.insertSelective(record);
	}

	@Override
	public ErrorHandlingBank selectByPrimaryKey(ErrorHandlingBankKey key) {
		return errorHandlingBankMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ErrorHandlingBank record) {
		return errorHandlingBankMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ErrorHandlingBank record) {
		return errorHandlingBankMapper.updateByPrimaryKey(record);
	}

	@Override
	public void deleteByBankIdAndTime(ErrorHandlingBank errorHandlingBank) {
		errorHandlingBankMapper.deleteByBankIdAndTime(errorHandlingBank);
	}
	@Override
	public List<ErrorHandlingBank> selectPage(ErrorHandlingBank record) {
		return errorHandlingBankMapper.selectPage(record);
	}

	@Override
	public int count(ErrorHandlingBank errorHandlingBank) {
		return errorHandlingBankMapper.count(errorHandlingBank);
	}

}
