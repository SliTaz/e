package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.db.domain.SysRoleUserKey;
import com.zbensoft.e.payment.db.mapper.SysRoleUserMapper;

@Service
public class SysRoleUserServiceImpl implements SysRoleUserService {

	@Autowired
	SysRoleUserMapper sysRoleUserMapper;

	@Override
	public int deleteByPrimaryKey(SysRoleUserKey key) {
		return sysRoleUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public SysRoleUserKey selectByPrimaryKey(SysRoleUserKey key) {
		return sysRoleUserMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(SysRoleUserKey record) {
		return sysRoleUserMapper.insert(record);
	}

	@Override
	public int insertSelective(SysRoleUserKey record) {
		return sysRoleUserMapper.insertSelective(record);
	}
	@Override
	public int count(SysRoleUserKey sysRoleUser) {
		return sysRoleUserMapper.count(sysRoleUser);
	}


	@Override
	public int updateByPrimaryKeySelective(SysRoleUserKey record) {

		return sysRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(SysRoleUserKey record) {

		return sysRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysRoleUserKey> selectPage(SysRoleUserKey record) {
		return sysRoleUserMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysRoleUserMapper.deleteAll();
	}

	@Override
	public List<SysRoleUserKey> selectByUserId(String userId) {
		return sysRoleUserMapper.selectByUserId(userId);
	}

}
