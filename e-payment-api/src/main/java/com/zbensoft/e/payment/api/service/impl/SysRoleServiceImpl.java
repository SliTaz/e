package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysRoleService;
import com.zbensoft.e.payment.db.domain.SysRole;
import com.zbensoft.e.payment.db.mapper.SysRoleMapper;
@Service
public class SysRoleServiceImpl implements SysRoleService {

	@Autowired
	SysRoleMapper sysRoleMapper;

	@Override
	public int deleteByPrimaryKey(String roleId) {
		return sysRoleMapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public int insert(SysRole record) {
		return sysRoleMapper.insert(record);
	}

	@Override
	public int count(SysRole role) {
		return sysRoleMapper.count(role);
	}

	@Override
	public int insertSelective(SysRole record) {
		return sysRoleMapper.insertSelective(record);
	}

	@Override
	public SysRole selectByPrimaryKey(String roleId) {
		return sysRoleMapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysRole record) {
		return sysRoleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysRole record) {
		return sysRoleMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysRole> selectPage(SysRole record) {
		return sysRoleMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysRoleMapper.deleteAll();
	}

	@Override
	public boolean isRoleExist(SysRole role) {
		return selectByRoleName(role.getName()) != null;
	}

	@Override
	public SysRole selectByRoleName(String name) {
		return sysRoleMapper.selectByRoleName(name);
	}
}
