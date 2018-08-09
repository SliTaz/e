package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantTradeService;
import com.zbensoft.e.payment.db.domain.MerchantTrade;
import com.zbensoft.e.payment.db.mapper.MerchantTradeMapper;

@Service
public class MerchantTradeServiceImpl implements MerchantTradeService {
	@Autowired
	MerchantTradeMapper merchantTradeMapper;

	@Override
	public int deleteByPrimaryKey(MerchantTrade key) {
		return merchantTradeMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(MerchantTrade record) {
		return merchantTradeMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantTrade record) {
		return merchantTradeMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantTrade record) {
		return merchantTradeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantTrade> selectPage(MerchantTrade record) {
		return merchantTradeMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		return merchantTradeMapper.deleteAll();
	}

	@Override
	public int count(MerchantTrade merchantTrade) {
		return merchantTradeMapper.count(merchantTrade);
	}
	@Override
	public List<MerchantTrade> selectPageTwo(MerchantTrade record) {
		return merchantTradeMapper.selectPageTwo(record);
	}


	@Override
	public int countTwo(MerchantTrade merchantTrade) {
		return merchantTradeMapper.countTwo(merchantTrade);
	}

	@Override
	public MerchantTrade selectByPrimaryKey(MerchantTrade merchantTrade) {
		return merchantTradeMapper.selectByPrimaryKey(merchantTrade);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantTrade currentMerchantTrade) {
		return merchantTradeMapper.updateByPrimaryKeySelective(currentMerchantTrade);
	}

	@Override
	public List<MerchantTrade> selectByUserId(String userId) {
		return merchantTradeMapper.selectByUserId(userId);
	}

}
