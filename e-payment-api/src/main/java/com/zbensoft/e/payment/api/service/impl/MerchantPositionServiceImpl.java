package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantPositionService;
import com.zbensoft.e.payment.db.domain.MerchantPosition;
import com.zbensoft.e.payment.db.mapper.MerchantPositionMapper;

@Service
public class MerchantPositionServiceImpl implements MerchantPositionService {
	@Autowired
	MerchantPositionMapper merchantPositionMapper;

	@Override
	public int deleteByPrimaryKey(String merchantPositionId) {
		
		return merchantPositionMapper.deleteByPrimaryKey(merchantPositionId);
	}

	@Override
	public int insert(MerchantPosition record) {
		
		return merchantPositionMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantPosition record) {
		
		return merchantPositionMapper.insertSelective(record);
	}

	@Override
	public MerchantPosition selectByPrimaryKey(String merchantPositionId) {
		
		return merchantPositionMapper.selectByPrimaryKey(merchantPositionId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantPosition record) {
		
		return merchantPositionMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantPosition record) {
		
		return merchantPositionMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantPosition> selectPage(MerchantPosition record) {
		return merchantPositionMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantPositionMapper.deleteAll();
	}

	@Override
	public int count(MerchantPosition merchantPosition) {
		return merchantPositionMapper.count(merchantPosition);
	}
	
}
