package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.mapper.BankInfoMapper;

@Service
public class BankInfoServiceImpl implements BankInfoService {
	@Autowired
	BankInfoMapper bankInfoMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {

		return bankInfoMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(BankInfo record) {

		return bankInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(BankInfo record) {

		return bankInfoMapper.insertSelective(record);
	}

	@Override
	public BankInfo selectByPrimaryKey(String bankId) {

		return bankInfoMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(BankInfo record) {

		return bankInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(BankInfo record) {

		return bankInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<BankInfo> selectPage(BankInfo record) {
		return bankInfoMapper.selectPage(record);
	}

	@Override
	public boolean isBankInfoExist(BankInfo bankInfo) {
		return findByName(bankInfo.getName()) != null;
	}

	private BankInfo findByName(String name) {
		return bankInfoMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		bankInfoMapper.deleteAll();

	}

	@Override
	public int count(BankInfo bankInfo) {
		return bankInfoMapper.count(bankInfo);
	}

	@Override
	public BankInfo selectByBankId(String bankId) {
		return bankInfoMapper.selectByBankId(bankId);
	}
}
