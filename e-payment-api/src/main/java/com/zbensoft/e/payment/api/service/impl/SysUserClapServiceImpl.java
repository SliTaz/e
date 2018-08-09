package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysUserClapService;
import com.zbensoft.e.payment.db.domain.SysUserClap;
import com.zbensoft.e.payment.db.mapper.SysUserClapMapper;

@Service
public class SysUserClapServiceImpl implements SysUserClapService {
	@Autowired
	SysUserClapMapper sysUserClapMapper;

	@Override
	public int deleteByPrimaryKey(String userId) {
		return sysUserClapMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(SysUserClap record) {
		return sysUserClapMapper.insert(record);
	}

	@Override
	public int count(SysUserClap userClap) {
		return sysUserClapMapper.count(userClap);
	}

	@Override
	public int insertSelective(SysUserClap record) {
		return sysUserClapMapper.insertSelective(record);
	}

	@Override
	public SysUserClap selectByPrimaryKey(String userId) {
		return sysUserClapMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysUserClap record) {
		return sysUserClapMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysUserClap record) {
		return sysUserClapMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysUserClap> selectPage(SysUserClap record) {
		return sysUserClapMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysUserClapMapper.deleteAll();
	}

}
