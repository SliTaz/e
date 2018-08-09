package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysUserBankCardService;
import com.zbensoft.e.payment.db.domain.SysUserBankCard;
import com.zbensoft.e.payment.db.mapper.SysUserBankCardMapper;

@Service
public class SysUserBankCardServiceImpl implements SysUserBankCardService {
	@Autowired
	SysUserBankCardMapper sysUserBankCardMapper;

	@Override
	public int deleteByPrimaryKey(String bankBindId) {
		return sysUserBankCardMapper.deleteByPrimaryKey(bankBindId);
	}

	@Override
	public int insert(SysUserBankCard record) {
		return sysUserBankCardMapper.insert(record);
	}

	@Override
	public int count(SysUserBankCard userBankCard) {
		return sysUserBankCardMapper.count(userBankCard);
	}

	@Override
	public int insertSelective(SysUserBankCard record) {
		return sysUserBankCardMapper.insertSelective(record);
	}

	@Override
	public SysUserBankCard selectByPrimaryKey(String bankBindId) {
		return sysUserBankCardMapper.selectByPrimaryKey(bankBindId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysUserBankCard record) {
		return sysUserBankCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysUserBankCard record) {
		return sysUserBankCardMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysUserBankCard> selectPage(SysUserBankCard record) {
		return sysUserBankCardMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysUserBankCardMapper.deleteAll();
	}

}
