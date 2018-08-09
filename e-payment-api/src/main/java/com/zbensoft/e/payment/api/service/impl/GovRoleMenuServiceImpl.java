package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GovRoleMenuService;
import com.zbensoft.e.payment.db.domain.GovRoleMenuKey;
import com.zbensoft.e.payment.db.mapper.GovRoleMenuMapper;
@Service
public class GovRoleMenuServiceImpl implements GovRoleMenuService {
@Autowired
GovRoleMenuMapper govRoleMenuMapper;
	@Override
	public int deleteByPrimaryKey(GovRoleMenuKey key) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(GovRoleMenuKey record) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.insert(record);
	}

	@Override
	public int insertSelective(GovRoleMenuKey record) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(GovRoleMenuKey record) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.updateByPrimaryKey(record);
	}


	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.deleteAll();
	}

	@Override
	public List<GovRoleMenuKey> selectPage(GovRoleMenuKey record) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.selectPage(record);
	}

	@Override
	public int count(GovRoleMenuKey role) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.count(role);
	}

	@Override
	public boolean isRoleExist(GovRoleMenuKey key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GovRoleMenuKey selectByPrimaryKey(String roleId) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(GovRoleMenuKey record) {
		// TODO Auto-generated method stub
		return govRoleMenuMapper.updateByPrimaryKeySelective(record);
	}

}
