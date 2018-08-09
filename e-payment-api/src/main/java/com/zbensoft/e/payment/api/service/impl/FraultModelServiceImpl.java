package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultModelService;
import com.zbensoft.e.payment.db.domain.FraultModel;
import com.zbensoft.e.payment.db.mapper.FraultModelMapper;

@Service
public class FraultModelServiceImpl implements FraultModelService {
	@Autowired
	FraultModelMapper fraultModelMapper;

	@Override
	public int deleteByPrimaryKey(String fraultModelId) {
		
		return fraultModelMapper.deleteByPrimaryKey(fraultModelId);
	}

	@Override
	public int insert(FraultModel record) {
		
		return fraultModelMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultModel record) {
		
		return fraultModelMapper.insertSelective(record);
	}

	@Override
	public FraultModel selectByPrimaryKey(String fraultModelId) {
		
		return fraultModelMapper.selectByPrimaryKey(fraultModelId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultModel record) {
		
		return fraultModelMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultModel record) {
		
		return fraultModelMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultModel> selectPage(FraultModel record) {
		return fraultModelMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		fraultModelMapper.deleteAll();
	}

	@Override
	public int count(FraultModel fraultModel) {
		return fraultModelMapper.count(fraultModel);
	}
	
}
