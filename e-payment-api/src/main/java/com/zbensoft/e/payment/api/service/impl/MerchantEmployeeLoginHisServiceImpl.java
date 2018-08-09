package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantEmployeeLoginHisService;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeLoginHis;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeLoginHisMapper;

@Service
public class MerchantEmployeeLoginHisServiceImpl implements MerchantEmployeeLoginHisService{
	@Autowired
	MerchantEmployeeLoginHisMapper merchantEmployeeLoginHisMapper;
	@Override
	public int deleteByPrimaryKey(String loginHisId) {
		return merchantEmployeeLoginHisMapper.deleteByPrimaryKey(loginHisId);
	}

	@Override
	public int insert(MerchantEmployeeLoginHis record) {
		return merchantEmployeeLoginHisMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantEmployeeLoginHis record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MerchantEmployeeLoginHis selectByPrimaryKey(String loginHisId) {
		return merchantEmployeeLoginHisMapper.selectByPrimaryKey(loginHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantEmployeeLoginHis record) {
		return merchantEmployeeLoginHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantEmployeeLoginHis record) {
		return merchantEmployeeLoginHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantEmployeeLoginHis> selectPage(MerchantEmployeeLoginHis merchantEmployeeLoginHis) {
		return merchantEmployeeLoginHisMapper.selectPage(merchantEmployeeLoginHis);
	}

	@Override
	public int count(MerchantEmployeeLoginHis merchantEmployeeLoginHis) {
		return merchantEmployeeLoginHisMapper.count(merchantEmployeeLoginHis);
	}

	@Override
	public void deleteAll() {
		merchantEmployeeLoginHisMapper.deleteAll();
		
	}
	
}