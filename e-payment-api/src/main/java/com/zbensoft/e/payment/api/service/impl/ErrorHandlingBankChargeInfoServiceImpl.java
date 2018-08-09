package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankChargeInfoService;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfoKey;
import com.zbensoft.e.payment.db.mapper.ErrorHandlingBankChargeInfoMapper;

@Service
public class ErrorHandlingBankChargeInfoServiceImpl implements ErrorHandlingBankChargeInfoService {

	@Autowired
	ErrorHandlingBankChargeInfoMapper errorHandlingBankChargeInfoMapper;
	
	@Override
	public int deleteByPrimaryKey(ErrorHandlingBankChargeInfoKey key) {
		return errorHandlingBankChargeInfoMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ErrorHandlingBankChargeInfo record) {
		return errorHandlingBankChargeInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(ErrorHandlingBankChargeInfo record) {
		return errorHandlingBankChargeInfoMapper.insertSelective(record);
	}

	@Override
	public ErrorHandlingBankChargeInfo selectByPrimaryKey(ErrorHandlingBankChargeInfoKey key) {
		return errorHandlingBankChargeInfoMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ErrorHandlingBankChargeInfo record) {
		return errorHandlingBankChargeInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ErrorHandlingBankChargeInfo record) {
		return errorHandlingBankChargeInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public void deleteByBankIdAndTime(ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo) {
		errorHandlingBankChargeInfoMapper.deleteByBankIdAndTime(errorHandlingBankChargeInfo);
	}

	@Override
	public List<ErrorHandlingBankChargeInfo> selectPage(ErrorHandlingBankChargeInfo record) {
		return errorHandlingBankChargeInfoMapper.selectPage(record);
	}

	@Override
	public int count(ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo) {
		return errorHandlingBankChargeInfoMapper.count(errorHandlingBankChargeInfo);
	}
	
	
}