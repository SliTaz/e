package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ShellCommandService;
import com.zbensoft.e.payment.db.domain.ShellCommand;
import com.zbensoft.e.payment.db.mapper.ShellCommandMapper;
@Service
public class ShellCommandServiceImpl implements ShellCommandService {
	@Autowired
	ShellCommandMapper shellCommandMapper;
	@Override
	public int deleteByPrimaryKey(String shellCode) {
		return shellCommandMapper.deleteByPrimaryKey(shellCode);
	}

	@Override
	public int insert(ShellCommand record) {
		return shellCommandMapper.insert(record);
	}

	@Override
	public int insertSelective(ShellCommand record) {
		return shellCommandMapper.insertSelective(record);
	}

	@Override
	public ShellCommand selectByPrimaryKey(String shellCode) {
		return shellCommandMapper.selectByPrimaryKey(shellCode);
	}

	@Override
	public int updateByPrimaryKeySelective(ShellCommand record) {
		return shellCommandMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ShellCommand record) {
		return shellCommandMapper.updateByPrimaryKey(record);
	}

	@Override
	public int count(ShellCommand record) {
		return shellCommandMapper.count(record);
	}

	@Override
	public List<ShellCommand> selectPage(ShellCommand record) {
		return shellCommandMapper.selectPage(record);
	}

	@Override
	public boolean isExist(ShellCommand record) {
		return selectByName(record.getName()) !=null;
	}

	@Override
	public ShellCommand selectByName(String name) {
		// TODO Auto-generated method stub
		return shellCommandMapper.selectByName(name);
	}

}
