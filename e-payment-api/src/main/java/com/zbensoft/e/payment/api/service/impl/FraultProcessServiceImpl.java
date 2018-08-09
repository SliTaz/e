package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultProcessService;
import com.zbensoft.e.payment.db.domain.FraultProcess;
import com.zbensoft.e.payment.db.mapper.FraultProcessMapper;

@Service
public class FraultProcessServiceImpl implements FraultProcessService {
	@Autowired
	FraultProcessMapper fraultProcessMapper;

	@Override
	public int deleteByPrimaryKey(String fraultProcessId) {
		
		return fraultProcessMapper.deleteByPrimaryKey(fraultProcessId);
	}

	@Override
	public int insert(FraultProcess record) {
		
		return fraultProcessMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultProcess record) {
		
		return fraultProcessMapper.insertSelective(record);
	}

	@Override
	public FraultProcess selectByPrimaryKey(String fraultProcessId) {
		
		return fraultProcessMapper.selectByPrimaryKey(fraultProcessId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultProcess record) {
		
		return fraultProcessMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultProcess record) {
		
		return fraultProcessMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultProcess> selectPage(FraultProcess record) {
		return fraultProcessMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		fraultProcessMapper.deleteAll();
	}

	@Override
	public int count(FraultProcess fraultProcess) {
		return fraultProcessMapper.count(fraultProcess);
	}
	
}
