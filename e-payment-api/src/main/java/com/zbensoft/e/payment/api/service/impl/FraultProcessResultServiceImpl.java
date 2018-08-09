package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultProcessResultService;
import com.zbensoft.e.payment.db.domain.FraultProcessResult;
import com.zbensoft.e.payment.db.mapper.FraultProcessResultMapper;

@Service
public class FraultProcessResultServiceImpl implements FraultProcessResultService {
	@Autowired
	FraultProcessResultMapper fraultProcessResultMapper;

	@Override
	public int deleteByPrimaryKey(FraultProcessResult key) {
		return fraultProcessResultMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(FraultProcessResult record) {
		return fraultProcessResultMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultProcessResult record) {
		return fraultProcessResultMapper.insertSelective(record);
	}

	@Override
	public FraultProcessResult selectByPrimaryKey(FraultProcessResult key) {
		return fraultProcessResultMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultProcessResult record) {
		return fraultProcessResultMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultProcessResult record) {
		return fraultProcessResultMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<FraultProcessResult> selectPage(FraultProcessResult record) {
		return fraultProcessResultMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		 fraultProcessResultMapper.deleteAll();
		
	}

	@Override
	public int count(FraultProcessResult fraultProcessResult) {
		return fraultProcessResultMapper.count(fraultProcessResult);
	}


	
}
