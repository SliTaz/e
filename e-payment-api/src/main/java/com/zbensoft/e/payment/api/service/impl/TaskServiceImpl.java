package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.TaskService;
import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.mapper.TaskMapper;
@Service
public class TaskServiceImpl implements TaskService {
@Autowired
TaskMapper taskMapper;
	@Override
	public int deleteByPrimaryKey(String taskId) {
		return taskMapper.deleteByPrimaryKey(taskId);
	}

	@Override
	public int insert(Task record) {
		return taskMapper.insert(record);
	}

	@Override
	public int insertSelective(Task record) {
		return taskMapper.insertSelective(record);
	}

	@Override
	public Task selectByPrimaryKey(String taskId) {
		return taskMapper.selectByPrimaryKey(taskId);
	}

	@Override
	public int updateByPrimaryKeySelective(Task record) {
		return taskMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Task record) {
		return taskMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return taskMapper.deleteAll();
	}

	@Override
	public int count(Task task) {
		return taskMapper.count(task);
	}

	@Override
	public Task selectByTaskName(String name) {
		return taskMapper.selectByTaskName(name);
	}

	@Override
	public List<Task> selectPage(Task record) {
		return taskMapper.selectPage(record);
	}

	@Override
	public boolean isExist(Task task) {
		return selectByTaskName(task.getName()) !=null;
	}

}
