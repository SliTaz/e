package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantRoleService;
import com.zbensoft.e.payment.db.domain.MerchantRole;
import com.zbensoft.e.payment.db.mapper.MerchantRoleMapper;
@Service
public class MerchantRoleServiceImpl implements MerchantRoleService {
@Autowired
MerchantRoleMapper merchantrolemapper;
	@Override
	public int deleteByPrimaryKey(String roleId) {
		// TODO Auto-generated method stub
		return merchantrolemapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public int insert(MerchantRole record) {
		// TODO Auto-generated method stub
		return merchantrolemapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantRole record) {
		// TODO Auto-generated method stub
		return merchantrolemapper.insertSelective(record);
	}

	@Override
	public MerchantRole selectByPrimaryKey(String roleId) {
		// TODO Auto-generated method stub
		return merchantrolemapper.selectByPrimaryKey(roleId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantRole record) {
		// TODO Auto-generated method stub
		return merchantrolemapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantRole record) {
		// TODO Auto-generated method stub
		return merchantrolemapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return merchantrolemapper.deleteAll();
	}

	@Override
	public int count(MerchantRole role) {
		// TODO Auto-generated method stub
		return merchantrolemapper.count(role);
	}

	@Override
	public List<MerchantRole> selectPage(MerchantRole record) {
		// TODO Auto-generated method stub
		return merchantrolemapper.selectPage(record);
	}

	@Override
	public boolean isRoleExist(MerchantRole role) {
		// TODO Auto-generated method stub
		return selectByMerName(role.getName()) != null;
	}

	@Override
	public MerchantRole selectByMerName(String name) {
		// TODO Auto-generated method stub
		return merchantrolemapper.selectByMerName(name);
	}

}
