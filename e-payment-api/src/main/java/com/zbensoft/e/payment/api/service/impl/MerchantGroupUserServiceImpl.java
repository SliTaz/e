package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerGroupUserService;
import com.zbensoft.e.payment.api.service.api.MerchantGroupUserService;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.MerchantGroupUserKey;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupUserMapper;
import com.zbensoft.e.payment.db.mapper.MerchantGroupUserMapper;


@Service
public class MerchantGroupUserServiceImpl implements MerchantGroupUserService {

	@Autowired
	MerchantGroupUserMapper merchantGroupUserMapper;
	
	@Override
	public int deleteByPrimaryKey(MerchantGroupUserKey key) {
		return merchantGroupUserMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(MerchantGroupUserKey record) {
		return merchantGroupUserMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantGroupUserKey record) {
		return merchantGroupUserMapper.insertSelective(record);
	}

	@Override
	public void deleteByGroupId(String merchantGroupId) {
		merchantGroupUserMapper.deleteByGroupId(merchantGroupId);
	}

	@Override
	public int count(MerchantGroupUserKey merchantGroupUserKey) {
		return merchantGroupUserMapper.count(merchantGroupUserKey);
	}

	@Override
	public List<MerchantGroupUserKey> selectPage(MerchantGroupUserKey merchantGroupUserKey) {
		return merchantGroupUserMapper.selectPage(merchantGroupUserKey);
	}

	@Override
	public void deleteMerchantGroupUser(MerchantGroupUserKey merchantGroupUserKey) {
		merchantGroupUserMapper.deleteMerchantGroupUser(merchantGroupUserKey);
	}

	@Override
	public List<MerchantGroupUserKey> selectByUserId(String userId) {
		return merchantGroupUserMapper.selectByUserId(userId);
	}

	

}
