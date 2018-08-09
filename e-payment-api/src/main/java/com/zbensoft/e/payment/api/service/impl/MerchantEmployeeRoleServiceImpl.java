package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleService;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRole;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeRoleMapper;

@Service
public class MerchantEmployeeRoleServiceImpl implements MerchantEmployeeRoleService {
	@Autowired
	MerchantEmployeeRoleMapper merchantEmployeeRoleMapper;

	@Override
	public int deleteByPrimaryKey(String roleId) {
		
		return merchantEmployeeRoleMapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public int insert(MerchantEmployeeRole record) {
		
		return merchantEmployeeRoleMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantEmployeeRole record) {
		
		return merchantEmployeeRoleMapper.insertSelective(record);
	}

	@Override
	public MerchantEmployeeRole selectByPrimaryKey(String roleId) {
		
		return merchantEmployeeRoleMapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantEmployeeRole record) {
		
		return merchantEmployeeRoleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantEmployeeRole record) {
		
		return merchantEmployeeRoleMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantEmployeeRole> selectPage(MerchantEmployeeRole record) {
		return merchantEmployeeRoleMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantEmployeeRoleMapper.deleteAll();
	}

	@Override
	public int count(MerchantEmployeeRole merchantEmployeeRole) {
		return merchantEmployeeRoleMapper.count(merchantEmployeeRole);
	}
	
}
