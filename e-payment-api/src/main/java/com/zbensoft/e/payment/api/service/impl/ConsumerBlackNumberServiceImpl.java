package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerBlackNumberService;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumber;
import com.zbensoft.e.payment.db.mapper.ConsumerBlackNumberMapper;

@Service
public class ConsumerBlackNumberServiceImpl implements ConsumerBlackNumberService {
	@Autowired
	ConsumerBlackNumberMapper consumerBlackNumberMapper;

	@Override
	public int deleteByPrimaryKey(String userId) {
		
		return consumerBlackNumberMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(ConsumerBlackNumber record) {
		
		return consumerBlackNumberMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerBlackNumber record) {
		
		return consumerBlackNumberMapper.insertSelective(record);
	}

	@Override
	public ConsumerBlackNumber selectByPrimaryKey(String userId) {
		
		return consumerBlackNumberMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerBlackNumber record) {
		
		return consumerBlackNumberMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerBlackNumber record) {
		
		return consumerBlackNumberMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerBlackNumber> selectPage(ConsumerBlackNumber record) {
		return consumerBlackNumberMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumerBlackNumberMapper.deleteAll();
	}

	@Override
	public int count(ConsumerBlackNumber consumerBlackNumber) {
		return consumerBlackNumberMapper.count(consumerBlackNumber);
	}

	@Override
	public boolean isExist(ConsumerBlackNumber consumerBlackNumber) {
		return selectByPrimaryKey(consumerBlackNumber.getUserId()) != null;
	}
	
}
