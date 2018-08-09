package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AlarmEmailManageService;
import com.zbensoft.e.payment.db.domain.AlarmEmailManage;
import com.zbensoft.e.payment.db.mapper.AlarmEmailManageMapper;

@Service
public class AlarmEmailMangeServiceImpl implements AlarmEmailManageService {

	@Autowired
	AlarmEmailManageMapper alarmEmailManageMapper;
	
	@Override
	public int deleteByPrimaryKey(String alarmEmailManageId) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.deleteByPrimaryKey(alarmEmailManageId);
	}

	@Override
	public int insert(AlarmEmailManage record) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.insert(record);
	}

	@Override
	public int insertSelective(AlarmEmailManage record) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.insertSelective(record);
	}

	@Override
	public AlarmEmailManage selectByPrimaryKey(String alarmEmailManageId) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.selectByPrimaryKey(alarmEmailManageId);
	}

	@Override
	public int updateByPrimaryKeySelective(AlarmEmailManage record) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(AlarmEmailManage record) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<AlarmEmailManage> selectPage(AlarmEmailManage record) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		alarmEmailManageMapper.deleteAll();
	}

	@Override
	public int count(AlarmEmailManage alarmType) {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.count(alarmType);
	}

	@Override
	public List<AlarmEmailManage> selectAll() {
		// TODO Auto-generated method stub
		return alarmEmailManageMapper.selectAll();
	}

}
