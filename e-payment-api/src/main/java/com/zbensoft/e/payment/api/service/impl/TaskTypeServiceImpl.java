package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.TaskTypeService;
import com.zbensoft.e.payment.db.domain.TaskType;
import com.zbensoft.e.payment.db.mapper.TaskTypeMapper;
@Service
public class TaskTypeServiceImpl implements TaskTypeService {
@Autowired
 TaskTypeMapper taskTypeMapper;
	@Override
	public int deleteByPrimaryKey(String taskTypeId) {
		return taskTypeMapper.deleteByPrimaryKey(taskTypeId);
	}

	@Override
	public int insert(TaskType record) {
		return taskTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(TaskType record) {
		return taskTypeMapper.insertSelective(record);
	}

	@Override
	public TaskType selectByPrimaryKey(String taskTypeId) {
		return taskTypeMapper.selectByPrimaryKey(taskTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(TaskType record) {
		return taskTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(TaskType record) {
		return taskTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return taskTypeMapper.deleteAll();
	}

	@Override
	public int count(TaskType taskType) {
		return taskTypeMapper.count(taskType);
	}

	@Override
	public TaskType selectByTaskName(String name) {
		return taskTypeMapper.selectByTaskName(name);
	}

	@Override
	public List<TaskType> selectPage(TaskType record) {
		return taskTypeMapper.selectPage(record);
	}

	@Override
	public boolean isExist(TaskType taskType) {
		return selectByTaskName(taskType.getName()) !=null;
	}

	@Override
	public List<TaskType> selectById(String taskTypeId) {
		return taskTypeMapper.selectById(taskTypeId);
	}

	@Override
	public int deleteByPrimary(TaskType taskType) {
		return taskTypeMapper.deleteByPrimary(taskType);
	}

}
