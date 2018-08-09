package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleRelService;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRoleRelKey;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeRoleRelMapper;

@Service
public class MerchantEmployeeRoleRelServiceImpl implements MerchantEmployeeRoleRelService {

	@Autowired
	MerchantEmployeeRoleRelMapper merchantEmployeeRoleRelMapper;

	@Override
	public int deleteByPrimaryKey(MerchantEmployeeRoleRelKey key) {
		return merchantEmployeeRoleRelMapper.deleteByPrimaryKey(key);
	}

	@Override
	public MerchantEmployeeRoleRelKey selectByPrimaryKey(MerchantEmployeeRoleRelKey key) {
		return merchantEmployeeRoleRelMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(MerchantEmployeeRoleRelKey record) {
		return merchantEmployeeRoleRelMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantEmployeeRoleRelKey record) {
		return merchantEmployeeRoleRelMapper.insertSelective(record);
	}
	@Override
	public int count(MerchantEmployeeRoleRelKey merchantEmployeeRoleRel) {
		return merchantEmployeeRoleRelMapper.count(merchantEmployeeRoleRel);
	}


	@Override
	public int updateByPrimaryKeySelective(MerchantEmployeeRoleRelKey record) {

		return merchantEmployeeRoleRelMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantEmployeeRoleRelKey record) {

		return merchantEmployeeRoleRelMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantEmployeeRoleRelKey> selectPage(MerchantEmployeeRoleRelKey record) {
		return merchantEmployeeRoleRelMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantEmployeeRoleRelMapper.deleteAll();
	}

	@Override
	public List<MerchantEmployeeRoleRelKey> selectByRoleId(String roleId) {
		return merchantEmployeeRoleRelMapper.selectByRoleId(roleId);
	}

	@Override
	public List<MerchantEmployeeRoleRelKey> selectByUserId(String userId) {
		return merchantEmployeeRoleRelMapper.selectByUserId(userId);
	}

}
