package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysUserLoginHisService;
import com.zbensoft.e.payment.db.domain.SysUserLoginHis;
import com.zbensoft.e.payment.db.mapper.SysUserLoginHisMapper;


@Service
public class SysUserLoginHisServiceImpl implements SysUserLoginHisService{
	@Autowired
	SysUserLoginHisMapper sysUserLoginHisMapper;
	
	
	@Override
	public int deleteByPrimaryKey(String consumerUserLoginHisId) {
		return sysUserLoginHisMapper.deleteByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int insert(SysUserLoginHis record) {
		return sysUserLoginHisMapper.insert(record);
	}

	@Override
	public int insertSelective(SysUserLoginHis record) {
		return sysUserLoginHisMapper.insertSelective(record);
	}

	@Override
	public SysUserLoginHis selectByPrimaryKey(String consumerUserLoginHisId) {
		return sysUserLoginHisMapper.selectByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysUserLoginHis record) {
		return sysUserLoginHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysUserLoginHis record) {
		return sysUserLoginHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysUserLoginHis> selectPage(SysUserLoginHis sysUserLoginHis) {
		return sysUserLoginHisMapper.selectPage(sysUserLoginHis);
	}

	@Override
	public int count(SysUserLoginHis sysUserLoginHis) {
		return sysUserLoginHisMapper.count(sysUserLoginHis);
	}

	@Override
	public void deleteAll() {
		sysUserLoginHisMapper.deleteAll();
	}
	
}