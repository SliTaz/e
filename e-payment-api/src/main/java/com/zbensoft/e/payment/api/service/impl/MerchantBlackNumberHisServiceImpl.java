package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantBlackNumberHisService;
import com.zbensoft.e.payment.db.domain.MerchantBlackNumberHis;
import com.zbensoft.e.payment.db.mapper.MerchantBlackNumberHisMapper;

@Service
public class MerchantBlackNumberHisServiceImpl implements MerchantBlackNumberHisService {
	@Autowired
	MerchantBlackNumberHisMapper merchantBlackNumberHisMapper;

	@Override
	public int deleteByPrimaryKey(String merchantBlackNumberHisId) {
		
		return merchantBlackNumberHisMapper.deleteByPrimaryKey(merchantBlackNumberHisId);
	}

	@Override
	public int insert(MerchantBlackNumberHis record) {
		
		return merchantBlackNumberHisMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantBlackNumberHis record) {
		
		return merchantBlackNumberHisMapper.insertSelective(record);
	}

	@Override
	public MerchantBlackNumberHis selectByPrimaryKey(String merchantBlackNumberHisId) {
		
		return merchantBlackNumberHisMapper.selectByPrimaryKey(merchantBlackNumberHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantBlackNumberHis record) {
		
		return merchantBlackNumberHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantBlackNumberHis record) {
		
		return merchantBlackNumberHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantBlackNumberHis> selectPage(MerchantBlackNumberHis record) {
		return merchantBlackNumberHisMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantBlackNumberHisMapper.deleteAll();
	}

	@Override
	public int count(MerchantBlackNumberHis merchantBlackNumberHis) {
		return merchantBlackNumberHisMapper.count(merchantBlackNumberHis);
	}
	
}
