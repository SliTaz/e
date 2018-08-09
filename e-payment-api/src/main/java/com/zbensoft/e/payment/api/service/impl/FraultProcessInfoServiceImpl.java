package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultProcessInfoService;
import com.zbensoft.e.payment.db.domain.FraultProcessInfo;
import com.zbensoft.e.payment.db.mapper.FraultProcessInfoMapper;

@Service
public class FraultProcessInfoServiceImpl implements FraultProcessInfoService {
	@Autowired
	FraultProcessInfoMapper fraultProcessInfoMapper;

	@Override
	public int deleteByPrimaryKey(String fraultProcessInfoId) {
		
		return fraultProcessInfoMapper.deleteByPrimaryKey(fraultProcessInfoId);
	}

	@Override
	public int insert(FraultProcessInfo record) {
		
		return fraultProcessInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultProcessInfo record) {
		
		return fraultProcessInfoMapper.insertSelective(record);
	}

	@Override
	public FraultProcessInfo selectByPrimaryKey(String fraultProcessInfoId) {
		
		return fraultProcessInfoMapper.selectByPrimaryKey(fraultProcessInfoId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultProcessInfo record) {
		
		return fraultProcessInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultProcessInfo record) {
		
		return fraultProcessInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultProcessInfo> selectPage(FraultProcessInfo record) {
		return fraultProcessInfoMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		fraultProcessInfoMapper.deleteAll();
	}

	@Override
	public int count(FraultProcessInfo fraultProcessInfo) {
		return fraultProcessInfoMapper.count(fraultProcessInfo);
	}
	
}
