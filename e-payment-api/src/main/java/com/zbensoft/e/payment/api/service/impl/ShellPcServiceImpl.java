package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ShellPcService;
import com.zbensoft.e.payment.db.domain.ShellPc;
import com.zbensoft.e.payment.db.mapper.ShellPcMapper;

@Service
public class ShellPcServiceImpl implements ShellPcService {

	@Autowired
	ShellPcMapper shellPcMapper;

	@Override
	public int deleteByPrimaryKey(String pcCode) {
		return shellPcMapper.deleteByPrimaryKey(pcCode);
	}

	@Override
	public int insert(ShellPc record) {
		return shellPcMapper.insert(record);
	}

	@Override
	public int insertSelective(ShellPc record) {
		return shellPcMapper.insertSelective(record);
	}

	@Override
	public ShellPc selectByPrimaryKey(String pcCode) {
		return shellPcMapper.selectByPrimaryKey(pcCode);
	}

	@Override
	public int updateByPrimaryKeySelective(ShellPc record) {
		return shellPcMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ShellPc record) {
		return shellPcMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ShellPc> selectPage(ShellPc record) {
		return shellPcMapper.selectPage(record);
	}

	@Override
	public int count(ShellPc shellPc) {
		return shellPcMapper.count(shellPc);
	}

}
