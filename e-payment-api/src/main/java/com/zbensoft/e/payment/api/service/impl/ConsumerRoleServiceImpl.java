package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerRoleService;
import com.zbensoft.e.payment.db.domain.ConsumerRole;
import com.zbensoft.e.payment.db.mapper.ConsumerRoleMapper;
@Service
public class ConsumerRoleServiceImpl implements ConsumerRoleService {
@Autowired
ConsumerRoleMapper consumeRoleMapper;
	@Override
	public int deleteByPrimaryKey(String roleId) {
		return consumeRoleMapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public int insert(ConsumerRole record) {
		return consumeRoleMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerRole record) {
		return consumeRoleMapper.insertSelective(record);
	}

	@Override
	public ConsumerRole selectByPrimaryKey(String roleId) {
		return consumeRoleMapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerRole record) {
		// TODO Auto-generated method stub
		return consumeRoleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerRole record) {
		return consumeRoleMapper.updateByPrimaryKey(record);
	}


	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return consumeRoleMapper.deleteAll();
	}

	@Override
	public List<ConsumerRole> selectPage(ConsumerRole record) {
		// TODO Auto-generated method stub
		return consumeRoleMapper.selectPage(record);
	}

	@Override
	public int count(ConsumerRole role) {
		// TODO Auto-generated method stub
		return consumeRoleMapper.count(role);
	}

	@Override
	public boolean isRoleExist(ConsumerRole role) {
		// TODO Auto-generated method stub
		return selectByConsumerRoleName(role.getName()) != null;
	}

	@Override
	public ConsumerRole selectByConsumerRoleName(String name) {
		// TODO Auto-generated method stub
		return consumeRoleMapper.selectByConsumerRoleName(name);
	}

}
