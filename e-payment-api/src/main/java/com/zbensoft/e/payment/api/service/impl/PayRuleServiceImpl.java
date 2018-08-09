package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayRuleService;
import com.zbensoft.e.payment.db.domain.PayRule;
import com.zbensoft.e.payment.db.mapper.PayRuleMapper;

@Service
public class PayRuleServiceImpl implements PayRuleService {
	@Autowired
	PayRuleMapper payRuleMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {
		
		return payRuleMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(PayRule record) {
		
		return payRuleMapper.insert(record);
	}

	@Override
	public int insertSelective(PayRule record) {
		
		return payRuleMapper.insertSelective(record);
	}

	@Override
	public PayRule selectByPrimaryKey(String bankId) {
		
		return payRuleMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(PayRule record) {
		
		return payRuleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayRule record) {
		
		return payRuleMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayRule> selectPage(PayRule record) {
		return payRuleMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		payRuleMapper.deleteAll();
	}

	@Override
	public int count(PayRule payRule) {
		return payRuleMapper.count(payRule);
	}
	
}
