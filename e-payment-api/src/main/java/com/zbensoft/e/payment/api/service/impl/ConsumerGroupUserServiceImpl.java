package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerGroupUserService;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupUserMapper;


@Service
public class ConsumerGroupUserServiceImpl implements ConsumerGroupUserService {

	@Autowired
	ConsumerGroupUserMapper consumerGroupUserMapper;

	@Override
	public int deleteByPrimaryKey(ConsumerGroupUserKey key) {
		return consumerGroupUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerGroupUserKey record) {
		return consumerGroupUserMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerGroupUserKey record) {
		return consumerGroupUserMapper.insertSelective(record);
	}

	@Override
	public void deleteByGroupId(String consumerGroupId) {
		consumerGroupUserMapper.deleteByGroupId(consumerGroupId);
	}

	@Override
	public int count(ConsumerGroupUserKey consumerGroupUserKey) {
		return consumerGroupUserMapper.count(consumerGroupUserKey);
	}

	@Override
	public List<ConsumerGroupUserKey> selectPage(ConsumerGroupUserKey consumerGroupUserKey) {
		return consumerGroupUserMapper.selectPage(consumerGroupUserKey);
	}

	@Override
	public void deleteConsumerGroupUser(ConsumerGroupUserKey consumerGroupUserKey) {
		consumerGroupUserMapper.deleteConsumerGroupUser(consumerGroupUserKey);
	}

	@Override
	public List<ConsumerGroupUserKey> selectByGroupId(String groupId) {
		return consumerGroupUserMapper.selectByGroupId(groupId);
	}
	@Override
	public List<ConsumerGroupUserKey> selectByUserId(String userId) {
		return consumerGroupUserMapper.selectByUserId(userId);
	}

}
