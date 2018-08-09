package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GovRoleUserService;
import com.zbensoft.e.payment.db.domain.GovRoleUserKey;
import com.zbensoft.e.payment.db.mapper.GovRoleUserMapper;

@Service
public class GovRoleUserServiceImpl implements GovRoleUserService {

	@Autowired
	GovRoleUserMapper govRoleUserMapper;

	@Override
	public int deleteByPrimaryKey(GovRoleUserKey key) {
		return govRoleUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public GovRoleUserKey selectByPrimaryKey(GovRoleUserKey key) {
		return govRoleUserMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(GovRoleUserKey record) {
		return govRoleUserMapper.insert(record);
	}

	@Override
	public int insertSelective(GovRoleUserKey record) {
		return govRoleUserMapper.insertSelective(record);
	}
	@Override
	public int count(GovRoleUserKey govRoleUser) {
		return govRoleUserMapper.count(govRoleUser);
	}


	@Override
	public int updateByPrimaryKeySelective(GovRoleUserKey record) {

		return govRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(GovRoleUserKey record) {

		return govRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GovRoleUserKey> selectPage(GovRoleUserKey record) {
		return govRoleUserMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		govRoleUserMapper.deleteAll();
	}

	@Override
	public List<GovRoleUserKey> selectByUserId(String userId) {
		return govRoleUserMapper.selectByUserId(userId);
	}

}
