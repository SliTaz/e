package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultMerchantService;
import com.zbensoft.e.payment.db.domain.FraultMerchant;
import com.zbensoft.e.payment.db.mapper.FraultMerchantMapper;
@Service
public class FraultMerchantServiceImpl implements FraultMerchantService {
@Autowired
FraultMerchantMapper  fraultMerchantMapper;
	@Override
	public int deleteByPrimaryKey(String fraultMerchantId) {
		
		return fraultMerchantMapper.deleteByPrimaryKey(fraultMerchantId);
	}

	@Override
	public int insert(FraultMerchant record) {
		
		return fraultMerchantMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultMerchant record) {
		
		return fraultMerchantMapper.insertSelective(record);
	}

	@Override
	public FraultMerchant selectByPrimaryKey(String fraultMerchantId) {
		
		return fraultMerchantMapper.selectByPrimaryKey(fraultMerchantId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultMerchant record) {
		
		return fraultMerchantMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultMerchant record) {
		
		return fraultMerchantMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		
		return fraultMerchantMapper.deleteAll();
	}

	@Override
	public int count(FraultMerchant number) {
		
		return fraultMerchantMapper.count(number);
	}

	@Override
	public List<FraultMerchant> selectPage(FraultMerchant number) {
		
		return fraultMerchantMapper.selectPage(number);
	}

}
