package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SysLogService;
import com.zbensoft.e.payment.db.domain.SysLog;
import com.zbensoft.e.payment.db.mapper.SysLogMapper;

@Service
public class SysLogServiceImpl implements SysLogService {
	@Autowired
	SysLogMapper sysLogMapper;

	@Override
	public int deleteByPrimaryKey(String sysLogId) {
		
		return sysLogMapper.deleteByPrimaryKey(sysLogId);
	}

	@Override
	public int insert(SysLog record) {
		
		return sysLogMapper.insert(record);
	}

	@Override
	public int insertSelective(SysLog record) {
		
		return sysLogMapper.insertSelective(record);
	}

	@Override
	public SysLog selectByPrimaryKey(String sysLogId) {
		
		return sysLogMapper.selectByPrimaryKey(sysLogId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysLog record) {
		
		return sysLogMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysLog record) {
		
		return sysLogMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysLog> selectPage(SysLog record) {
		return sysLogMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysLogMapper.deleteAll();
	}

	@Override
	public int count(SysLog sysLog) {
		return sysLogMapper.count(sysLog);
	}
	
}
