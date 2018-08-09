package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BankCardTypeService;
import com.zbensoft.e.payment.db.domain.BankCardType;
import com.zbensoft.e.payment.db.mapper.BankCardTypeMapper;
@Service  
public class BankCardTypeServiceImpl implements BankCardTypeService {
	@Autowired
	BankCardTypeMapper bankCardTypeMapper;
	@Override
	public int deleteByPrimaryKey(Integer bankCardTypeId) {
		return bankCardTypeMapper.deleteByPrimaryKey(bankCardTypeId);
	}

	@Override
	public int insert(BankCardType record) {
		return bankCardTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(BankCardType record) {
		return bankCardTypeMapper.insertSelective(record);
	}

	@Override
	public BankCardType selectByPrimaryKey(Integer bankCardTypeId) {
		return bankCardTypeMapper.selectByPrimaryKey(bankCardTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(BankCardType record) {
		return bankCardTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(BankCardType record) {
		return bankCardTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return bankCardTypeMapper.deleteAll();
	}

	@Override
	public int count(BankCardType record) {
		return bankCardTypeMapper.count(record);
	}

	@Override
	public List<BankCardType> selectPage(BankCardType record) {
		return bankCardTypeMapper.selectPage(record);
	}

	@Override
	public boolean isExist(BankCardType bankCardType) {
		return selectByName(bankCardType.getName()) !=null;
	}

	@Override
	public BankCardType selectByName(String name) {
		return bankCardTypeMapper.selectByName(name);
	}

}
