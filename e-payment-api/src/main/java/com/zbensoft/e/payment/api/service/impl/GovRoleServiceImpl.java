package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GovRoleService;
import com.zbensoft.e.payment.db.domain.GovRole;
import com.zbensoft.e.payment.db.domain.SysRole;
import com.zbensoft.e.payment.db.mapper.GovMenuMapper;
import com.zbensoft.e.payment.db.mapper.GovRoleMapper;
@Service
public class GovRoleServiceImpl implements GovRoleService {
@Autowired
GovRoleMapper govrolemapper;
	@Override
	public int deleteByPrimaryKey(String roleId) {
		return govrolemapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public int insert(GovRole record) {
		return govrolemapper.insert(record);
	}

	@Override
	public int insertSelective(GovRole record) {
		return govrolemapper.insertSelective(record);
	}

	@Override
	public GovRole selectByPrimaryKey(String roleId) {
		return govrolemapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(GovRole record) {
		return govrolemapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GovRole record) {
		return govrolemapper.updateByPrimaryKey(record);
	}


	@Override
	public int deleteAll() {
		return govrolemapper.deleteAll();
	}

	@Override
	public List<GovRole> selectPage(GovRole record) {
		return govrolemapper.selectPage(record);
	}

	@Override
	public int count(GovRole role) {
		return govrolemapper.count(role);
	}

	@Override
	public boolean isRoleExist(GovRole role) {
		return selectByRovName(role.getName())!=null;
	}

	@Override
	public GovRole selectByRovName(String anme) {
		return govrolemapper.selectByRovName(anme);
	}


}
