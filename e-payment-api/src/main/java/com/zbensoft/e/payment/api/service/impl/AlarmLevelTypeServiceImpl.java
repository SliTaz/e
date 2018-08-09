package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AlarmLevelTypeService;
import com.zbensoft.e.payment.db.domain.AlarmLevelType;
import com.zbensoft.e.payment.db.mapper.AlarmLevelTypeMapper;

@Service
public class AlarmLevelTypeServiceImpl implements AlarmLevelTypeService {
	@Autowired
	AlarmLevelTypeMapper alarmLevelTypeMapper;

	@Override
	public int deleteByPrimaryKey(AlarmLevelType key) {
		return alarmLevelTypeMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(AlarmLevelType record) {
		return alarmLevelTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(AlarmLevelType record) {
		return alarmLevelTypeMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(AlarmLevelType record) {
		return alarmLevelTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<AlarmLevelType> selectPage(AlarmLevelType record) {
		return alarmLevelTypeMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		return alarmLevelTypeMapper.deleteAll();
	}

	@Override
	public int count(AlarmLevelType alarmLevelType) {
		return alarmLevelTypeMapper.count(alarmLevelType);
	}

	@Override
	public AlarmLevelType selectByPrimaryKey(AlarmLevelType alarmLevelType) {
		return alarmLevelTypeMapper.selectByPrimaryKey(alarmLevelType);
	}

	@Override
	public int updateByPrimaryKeySelective(AlarmLevelType currentAlarmLevelType) {
		return alarmLevelTypeMapper.updateByPrimaryKeySelective(currentAlarmLevelType);
	}

	@Override
	public List<AlarmLevelType> selectByAlarmLevelId(String alarmLevelId) {
		return alarmLevelTypeMapper.selectByAlarmLevelId(alarmLevelId);
	}

	@Override
	public List<AlarmLevelType> selectAll() {
		return alarmLevelTypeMapper.selectAll();
	}

}
