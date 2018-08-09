package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysRoleMenuService;
import com.zbensoft.e.payment.db.domain.SysRoleMenu;
import com.zbensoft.e.payment.db.domain.SysRoleMenuKey;
import com.zbensoft.e.payment.db.mapper.SysRoleMenuMapper;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

	@Autowired
	SysRoleMenuMapper sysRoleMenuMapper;

	@Override
	public int deleteByPrimaryKey(SysRoleMenuKey key) {
		return sysRoleMenuMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(SysRoleMenu record) {
		return sysRoleMenuMapper.insert(record);
	}

	@Override
	public int count(SysRoleMenu roleMenu) {
		return sysRoleMenuMapper.count(roleMenu);
	}

	@Override
	public int insertSelective(SysRoleMenu record) {
		return sysRoleMenuMapper.insertSelective(record);
	}

	@Override
	public SysRoleMenu selectByPrimaryKey(SysRoleMenuKey key) {
		return sysRoleMenuMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(SysRoleMenu record) {
		return sysRoleMenuMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysRoleMenu record) {
		return sysRoleMenuMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysRoleMenu> selectPage(SysRoleMenu record) {
		return sysRoleMenuMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysRoleMenuMapper.deleteAll();
	}


	@Override
	public boolean isRoleExist(SysRoleMenu rolemenu) {
		return false;
	}
}