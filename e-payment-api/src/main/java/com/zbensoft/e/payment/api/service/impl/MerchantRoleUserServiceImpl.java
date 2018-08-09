package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantRoleUserService;
import com.zbensoft.e.payment.db.domain.MerchantRoleUserKey;
import com.zbensoft.e.payment.db.mapper.MerchantRoleUserMapper;

@Service
public class MerchantRoleUserServiceImpl implements MerchantRoleUserService {

	@Autowired
	MerchantRoleUserMapper merchantRoleUserMapper;

	@Override
	public int deleteByPrimaryKey(MerchantRoleUserKey key) {
		return merchantRoleUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public MerchantRoleUserKey selectByPrimaryKey(MerchantRoleUserKey key) {
		return merchantRoleUserMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(MerchantRoleUserKey record) {
		return merchantRoleUserMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantRoleUserKey record) {
		return merchantRoleUserMapper.insertSelective(record);
	}
	@Override
	public int count(MerchantRoleUserKey merchantRoleUser) {
		return merchantRoleUserMapper.count(merchantRoleUser);
	}


	@Override
	public int updateByPrimaryKeySelective(MerchantRoleUserKey record) {

		return merchantRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantRoleUserKey record) {

		return merchantRoleUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantRoleUserKey> selectPage(MerchantRoleUserKey record) {
		return merchantRoleUserMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantRoleUserMapper.deleteAll();
	}

	@Override
	public List<MerchantRoleUserKey> selectByUserId(String userId) {
		return merchantRoleUserMapper.selectByUserId(userId);
	}

}
