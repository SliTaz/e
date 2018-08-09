package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultInfoService;
import com.zbensoft.e.payment.db.domain.FraultInfo;
import com.zbensoft.e.payment.db.mapper.FraultInfoMapper;

@Service
public class FraultInfoServiceImpl implements FraultInfoService {
	@Autowired
	FraultInfoMapper fraultInfoMapper;

	@Override
	public int deleteByPrimaryKey(String fraultInfoId) {
		
		return fraultInfoMapper.deleteByPrimaryKey(fraultInfoId);
	}

	@Override
	public int insert(FraultInfo record) {
		
		return fraultInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultInfo record) {
		
		return fraultInfoMapper.insertSelective(record);
	}

	@Override
	public FraultInfo selectByPrimaryKey(String fraultInfoId) {
		
		return fraultInfoMapper.selectByPrimaryKey(fraultInfoId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultInfo record) {
		
		return fraultInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultInfo record) {
		
		return fraultInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultInfo> selectPage(FraultInfo record) {
		return fraultInfoMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		fraultInfoMapper.deleteAll();
	}

	@Override
	public int count(FraultInfo fraultInfo) {
		return fraultInfoMapper.count(fraultInfo);
	}
	
}
