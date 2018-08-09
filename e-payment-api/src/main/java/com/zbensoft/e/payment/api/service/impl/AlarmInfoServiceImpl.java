package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AlarmInfoService;
import com.zbensoft.e.payment.db.domain.AlarmInfo;
import com.zbensoft.e.payment.db.mapper.AlarmInfoMapper;

@Service
public class AlarmInfoServiceImpl implements AlarmInfoService {

	@Autowired
	AlarmInfoMapper alarmInfoMapper;

	@Override
	public int deleteByPrimaryKey(String alarmInfoCode) {
		return alarmInfoMapper.deleteByPrimaryKey(alarmInfoCode);
	}

	@Override
	public int insert(AlarmInfo record) {
		return alarmInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(AlarmInfo record) {
		return alarmInfoMapper.insertSelective(record);
	}

	@Override
	public AlarmInfo selectByPrimaryKey(String alarmInfoCode) {
		return alarmInfoMapper.selectByPrimaryKey(alarmInfoCode);
	}

	@Override
	public int updateByPrimaryKeySelective(AlarmInfo record) {
		return alarmInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(AlarmInfo record) {
		return alarmInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<AlarmInfo> selectPage(AlarmInfo record) {
		return alarmInfoMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		alarmInfoMapper.deleteAll();
	}

	@Override
	public int count(AlarmInfo alarmInfo) {
		return alarmInfoMapper.count(alarmInfo);
	}

	@Override
	public List<AlarmInfo> selectNewestRecords(AlarmInfo alarmInfo) {
		return alarmInfoMapper.selectNewestRecords(alarmInfo);
	}

	@Override
	public int getRecordsCount(AlarmInfo alarmInfo) {
		return alarmInfoMapper.getRecordsCount(alarmInfo);
	}

}
