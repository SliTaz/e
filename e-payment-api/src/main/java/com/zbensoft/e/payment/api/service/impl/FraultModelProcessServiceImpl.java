package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultModelProcessService;
import com.zbensoft.e.payment.db.domain.FraultModelProcess;
import com.zbensoft.e.payment.db.mapper.FraultModelProcessMapper;

@Service
public class FraultModelProcessServiceImpl implements FraultModelProcessService {
	@Autowired
	FraultModelProcessMapper fraultModelProcessMapper;

	@Override
	public int deleteByPrimaryKey(FraultModelProcess key) {
		return fraultModelProcessMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(FraultModelProcess record) {
		return fraultModelProcessMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultModelProcess record) {
		return fraultModelProcessMapper.insertSelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultModelProcess record) {
		return fraultModelProcessMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultModelProcess> selectPage(FraultModelProcess record) {
		return fraultModelProcessMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		return fraultModelProcessMapper.deleteAll();
	}

	@Override
	public int count(FraultModelProcess fraultModelProcess) {
		return fraultModelProcessMapper.count(fraultModelProcess);
	}

	@Override
	public FraultModelProcess selectByPrimaryKey(FraultModelProcess fraultModelProcess) {
		return fraultModelProcessMapper.selectByPrimaryKey(fraultModelProcess);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultModelProcess currentFraultModelProcess) {
		return fraultModelProcessMapper.updateByPrimaryKeySelective(currentFraultModelProcess);
	}

	@Override
	public List<FraultModelProcess> selectByModelId(String modelId) {
		return fraultModelProcessMapper.selectByModelId(modelId);
	}

}
