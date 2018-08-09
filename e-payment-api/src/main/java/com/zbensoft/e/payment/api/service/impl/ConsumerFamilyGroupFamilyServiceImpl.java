package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupFamilyService;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroupFamilyKey;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyGroupFamilyMapper;


@Service
public class ConsumerFamilyGroupFamilyServiceImpl implements ConsumerFamilyGroupFamilyService {

	@Autowired
	ConsumerFamilyGroupFamilyMapper consumerFamilyGroupFamilyMapper;
	
	@Override
	public int deleteByPrimaryKey(ConsumerFamilyGroupFamilyKey key) {
		return consumerFamilyGroupFamilyMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerFamilyGroupFamilyKey record) {
		return consumerFamilyGroupFamilyMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerFamilyGroupFamilyKey record) {
		return consumerFamilyGroupFamilyMapper.insertSelective(record);
	}

	@Override
	public void deleteByGroupId(String consumerFamilyGroupId) {
		consumerFamilyGroupFamilyMapper.deleteByGroupId(consumerFamilyGroupId);
	}

	@Override
	public int count(ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey) {
		return consumerFamilyGroupFamilyMapper.count(consumerFamilyGroupFamilyKey);
	}

	@Override
	public List<ConsumerFamilyGroupFamilyKey> selectPage(ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey) {
		return consumerFamilyGroupFamilyMapper.selectPage(consumerFamilyGroupFamilyKey);
	}

	@Override
	public void deleteConsumerFamilyGroupFamily(ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey) {
		consumerFamilyGroupFamilyMapper.deleteConsumerFamilyGroupFamily(consumerFamilyGroupFamilyKey);
	}

	@Override
	public List<ConsumerFamilyGroupFamilyKey> selectByGroupId(String groupId) {
		return consumerFamilyGroupFamilyMapper.selectByGroupId(groupId);
	}

	

}
