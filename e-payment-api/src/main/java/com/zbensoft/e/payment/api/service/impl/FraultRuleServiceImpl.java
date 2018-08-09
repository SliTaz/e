package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultRuleService;
import com.zbensoft.e.payment.db.domain.FraultRule;
import com.zbensoft.e.payment.db.mapper.FraultRuleMapper;

@Service
public class FraultRuleServiceImpl implements FraultRuleService {
	@Autowired
	FraultRuleMapper fraultRuleMapper;

	@Override
	public int deleteByPrimaryKey(String fraultRuleId) {
		
		return fraultRuleMapper.deleteByPrimaryKey(fraultRuleId);
	}

	@Override
	public int insert(FraultRule record) {
		
		return fraultRuleMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultRule record) {
		
		return fraultRuleMapper.insertSelective(record);
	}

	@Override
	public FraultRule selectByPrimaryKey(String fraultRuleId) {
		
		return fraultRuleMapper.selectByPrimaryKey(fraultRuleId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultRule record) {
		
		return fraultRuleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultRule record) {
		
		return fraultRuleMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultRule> selectPage(FraultRule record) {
		return fraultRuleMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		fraultRuleMapper.deleteAll();
	}

	@Override
	public int count(FraultRule fraultRule) {
		return fraultRuleMapper.count(fraultRule);
	}
	
}
