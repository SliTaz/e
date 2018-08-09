package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankTradeInfoService;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfoKey;
import com.zbensoft.e.payment.db.mapper.ErrorHandlingBankTradeInfoMapper;

@Service
public class ErrorHandlingBankTradeInfoServiceImpl implements ErrorHandlingBankTradeInfoService {
	@Autowired
	ErrorHandlingBankTradeInfoMapper errorHandlingBankTradeInfoMapper;

	@Override
	public int deleteByPrimaryKey(ErrorHandlingBankTradeInfoKey key) {
		return errorHandlingBankTradeInfoMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ErrorHandlingBankTradeInfo record) {
		return errorHandlingBankTradeInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(ErrorHandlingBankTradeInfo record) {
		return errorHandlingBankTradeInfoMapper.insertSelective(record);
	}

	@Override
	public ErrorHandlingBankTradeInfo selectByPrimaryKey(ErrorHandlingBankTradeInfoKey key) {
		return errorHandlingBankTradeInfoMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ErrorHandlingBankTradeInfo record) {
		return errorHandlingBankTradeInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ErrorHandlingBankTradeInfo record) {
		return errorHandlingBankTradeInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public void deleteByBankIdAndTime(ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo) {
		errorHandlingBankTradeInfoMapper.deleteByBankIdAndTime(errorHandlingBankTradeInfo);
		
	}

	@Override
	public List<ErrorHandlingBankTradeInfo> selectPage(ErrorHandlingBankTradeInfo record) {
		return errorHandlingBankTradeInfoMapper.selectPage(record);
	}

	@Override
	public int count(ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo) {
		return errorHandlingBankTradeInfoMapper.count(errorHandlingBankTradeInfo);
	}

}
