package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.mapper.ConsumerRoleUserMapper;

@Service
public class ConsumerRoleUserServiceImpl implements ConsumerRoleUserService {

	@Autowired
	ConsumerRoleUserMapper consumeRoleUserMapper;

	@Override
	public int deleteByPrimaryKey(ConsumerRoleUserKey key) {
		return consumeRoleUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public ConsumerRoleUserKey selectByPrimaryKey(ConsumerRoleUserKey key) {
		return consumeRoleUserMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerRoleUserKey record) {
		return consumeRoleUserMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerRoleUserKey record) {
		return consumeRoleUserMapper.insertSelective(record);
	}
	@Override
	public int count(ConsumerRoleUserKey consumeRoleUser) {
		return consumeRoleUserMapper.count(consumeRoleUser);
	}


	@Override
	public int updateByPrimaryKeySelective(ConsumerRoleUserKey record) {

		return consumeRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerRoleUserKey record) {

		return consumeRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerRoleUserKey> selectPage(ConsumerRoleUserKey record) {
		return consumeRoleUserMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumeRoleUserMapper.deleteAll();
	}
	@Override
	public List<ConsumerRoleUserKey> selectByUserId(String userId) {
		return consumeRoleUserMapper.selectByUserId(userId);
	}

}
