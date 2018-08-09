package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantUserLoginHisService;
import com.zbensoft.e.payment.db.domain.MerchantUserLoginHis;
import com.zbensoft.e.payment.db.mapper.MerchantUserLoginHisMapper;

@Service
public class MerchantUserLoginHisServiceImpl implements MerchantUserLoginHisService{
	@Autowired
	MerchantUserLoginHisMapper merchantUserLoginHisMapper;
	@Override
	public int deleteByPrimaryKey(String consumerUserLoginHisId) {
		return merchantUserLoginHisMapper.deleteByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int insert(MerchantUserLoginHis record) {
		return merchantUserLoginHisMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantUserLoginHis record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MerchantUserLoginHis selectByPrimaryKey(String consumerUserLoginHisId) {
		return merchantUserLoginHisMapper.selectByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantUserLoginHis record) {
		return merchantUserLoginHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantUserLoginHis record) {
		return merchantUserLoginHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantUserLoginHis> selectPage(MerchantUserLoginHis merchantUserLoginHis) {
		return merchantUserLoginHisMapper.selectPage(merchantUserLoginHis);
	}

	@Override
	public int count(MerchantUserLoginHis merchantUserLoginHis) {
		return merchantUserLoginHisMapper.count(merchantUserLoginHis);
	}

	@Override
	public void deleteAll() {
		merchantUserLoginHisMapper.deleteAll();
		
	}

	@Override
	public int countDistin(MerchantUserLoginHis consumerUserLoginHisSer) {
		return merchantUserLoginHisMapper.countDistin(consumerUserLoginHisSer);
	}
	
}