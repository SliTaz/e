package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultModelRuleService;
import com.zbensoft.e.payment.db.domain.FraultModelRule;
import com.zbensoft.e.payment.db.mapper.FraultModelRuleMapper;

@Service
public class FraultModelRuleServiceImpl implements FraultModelRuleService {
	@Autowired
	FraultModelRuleMapper fraultModelRuleMapper;

	@Override
	public int deleteByPrimaryKey(FraultModelRule key) {
		return fraultModelRuleMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(FraultModelRule record) {
		return fraultModelRuleMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultModelRule record) {
		return fraultModelRuleMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultModelRule record) {
		return fraultModelRuleMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultModelRule> selectPage(FraultModelRule record) {
		return fraultModelRuleMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		return fraultModelRuleMapper.deleteAll();
	}

	@Override
	public int count(FraultModelRule fraultModelRule) {
		return fraultModelRuleMapper.count(fraultModelRule);
	}

	@Override
	public FraultModelRule selectByPrimaryKey(FraultModelRule fraultModelRule) {
		return fraultModelRuleMapper.selectByPrimaryKey(fraultModelRule);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultModelRule currentPayAppGatway) {
		return fraultModelRuleMapper.updateByPrimaryKeySelective(currentPayAppGatway);
	}

	@Override
	public List<FraultModelRule> selectByModelId(String payAppId) {
		return fraultModelRuleMapper.selectByModelId(payAppId);
	}

	@Override
	public List<FraultModelRule> selectByRuleId(String selectByRuleId) {
		return fraultModelRuleMapper.selectByRuleId(selectByRuleId);
	}

}
