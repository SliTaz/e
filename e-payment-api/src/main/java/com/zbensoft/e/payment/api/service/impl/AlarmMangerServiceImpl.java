package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AlarmMangerService;
import com.zbensoft.e.payment.db.domain.AlarmManger;
import com.zbensoft.e.payment.db.mapper.AlarmMangerMapper;

@Service
public class AlarmMangerServiceImpl implements AlarmMangerService {

	@Autowired
	AlarmMangerMapper alarmMangerMapper;

	@Override
	public int deleteByPrimaryKey(String alarmKey) {
		return alarmMangerMapper.deleteByPrimaryKey(alarmKey);
	}

	@Override
	public int insert(AlarmManger record) {
		return alarmMangerMapper.insert(record);
	}

	@Override
	public int insertSelective(AlarmManger record) {
		return alarmMangerMapper.insertSelective(record);
	}

	@Override
	public AlarmManger selectByPrimaryKey(String alarmKey) {
		return alarmMangerMapper.selectByPrimaryKey(alarmKey);
	}

	@Override
	public int updateByPrimaryKeySelective(AlarmManger record) {
		return alarmMangerMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(AlarmManger record) {
		return alarmMangerMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<AlarmManger> selectPage(AlarmManger record) {
		return alarmMangerMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		alarmMangerMapper.deleteAll();
	}

	@Override
	public int count(AlarmManger alarmManger) {
		return alarmMangerMapper.count(alarmManger);
	}

	@Override
	public List<AlarmManger> selectAll() {
		return alarmMangerMapper.selectAll();
	}

}
