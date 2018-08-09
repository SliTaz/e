package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AlarmLevelService;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.mapper.AlarmLevelMapper;

@Service
public class AlarmLevelServiceImpl implements AlarmLevelService {
	@Autowired
	AlarmLevelMapper alarmLevelMapper;

	@Override
	public int deleteByPrimaryKey(String alarmLevelCode) {
		
		return alarmLevelMapper.deleteByPrimaryKey(alarmLevelCode);
	}

	@Override
	public int insert(AlarmLevel record) {
		
		return alarmLevelMapper.insert(record);
	}

	@Override
	public int insertSelective(AlarmLevel record) {
		
		return alarmLevelMapper.insertSelective(record);
	}

	@Override
	public AlarmLevel selectByPrimaryKey(String alarmLevelCode) {
		
		return alarmLevelMapper.selectByPrimaryKey(alarmLevelCode);
	}

	@Override
	public int updateByPrimaryKeySelective(AlarmLevel record) {
		
		return alarmLevelMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(AlarmLevel record) {
		
		return alarmLevelMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<AlarmLevel> selectPage(AlarmLevel record) {
		return alarmLevelMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		alarmLevelMapper.deleteAll();
	}

	@Override
	public int count(AlarmLevel alarmLevel) {
		return alarmLevelMapper.count(alarmLevel);
	}

	@Override
	public List<AlarmLevel> selectAll() {
		return alarmLevelMapper.selectAll();
	}
	
}
