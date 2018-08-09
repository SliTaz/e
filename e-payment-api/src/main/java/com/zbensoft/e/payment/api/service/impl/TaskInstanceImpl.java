package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.TaskInstanceService;
import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.domain.TaskInstance;
import com.zbensoft.e.payment.db.mapper.TaskInstanceMapper;
@Service
public class TaskInstanceImpl implements TaskInstanceService {
@Autowired
TaskInstanceMapper taskInstanceMapper;
	@Override
	public int deleteByPrimaryKey(String taskInstanceId) {
		return taskInstanceMapper.deleteByPrimaryKey(taskInstanceId);
	}

	@Override
	public int insert(TaskInstance record) {
		return taskInstanceMapper.insert(record);
	}

	@Override
	public int insertSelective(TaskInstance record) {
		return taskInstanceMapper.insertSelective(record);
	}

	@Override
	public TaskInstance selectByPrimaryKey(String taskInstanceId) {
		return taskInstanceMapper.selectByPrimaryKey(taskInstanceId);
	}

	@Override
	public int updateByPrimaryKeySelective(TaskInstance record) {
		return taskInstanceMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(TaskInstance record) {
		return taskInstanceMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return taskInstanceMapper.deleteAll();
	}

	@Override
	public int count(TaskInstance instance) {
		return taskInstanceMapper.count(instance);
	}

	@Override
	public List<TaskInstance> selectPage(TaskInstance record) {
		return taskInstanceMapper.selectPage(record);
	}

}
