package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantBlackNumberService;
import com.zbensoft.e.payment.db.domain.MerchantBlackNumber;
import com.zbensoft.e.payment.db.mapper.MerchantBlackNumberMapper;

@Service
public class MerchantBlackNumberServiceImpl implements MerchantBlackNumberService {
	@Autowired
	MerchantBlackNumberMapper merchantBlackNumberMapper;

	@Override
	public int deleteByPrimaryKey(String userId) {
		
		return merchantBlackNumberMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(MerchantBlackNumber record) {
		
		return merchantBlackNumberMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantBlackNumber record) {
		
		return merchantBlackNumberMapper.insertSelective(record);
	}

	@Override
	public MerchantBlackNumber selectByPrimaryKey(String userId) {
		
		return merchantBlackNumberMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantBlackNumber record) {
		
		return merchantBlackNumberMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantBlackNumber record) {
		
		return merchantBlackNumberMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantBlackNumber> selectPage(MerchantBlackNumber record) {
		return merchantBlackNumberMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantBlackNumberMapper.deleteAll();
	}

	@Override
	public int count(MerchantBlackNumber merchantBlackNumber) {
		return merchantBlackNumberMapper.count(merchantBlackNumber);
	}

	@Override
	public boolean isExist(MerchantBlackNumber merchantBlackNumber) {
		return selectByPrimaryKey(merchantBlackNumber.getUserId()) != null;
	}
	
}
