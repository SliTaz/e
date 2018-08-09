package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerUserLoginHisService;
import com.zbensoft.e.payment.db.domain.ConsumerUserLoginHis;
import com.zbensoft.e.payment.db.mapper.ConsumerUserLoginHisMapper;

@Service
public class ConsumerUserLoginHisServiceImpl implements ConsumerUserLoginHisService{
	@Autowired
	ConsumerUserLoginHisMapper consumerUserLoginHisMapper;
	@Override
	public int deleteByPrimaryKey(String consumerUserLoginHisId) {
		return consumerUserLoginHisMapper.deleteByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int insert(ConsumerUserLoginHis record) {
		return consumerUserLoginHisMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerUserLoginHis record) {
		return consumerUserLoginHisMapper.insertSelective(record);
	}

	@Override
	public ConsumerUserLoginHis selectByPrimaryKey(String consumerUserLoginHisId) {
		return consumerUserLoginHisMapper.selectByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerUserLoginHis record) {
		return consumerUserLoginHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerUserLoginHis record) {
		return consumerUserLoginHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUserLoginHis> selectPage(ConsumerUserLoginHis consumerUserLoginHis) {
		return consumerUserLoginHisMapper.selectPage(consumerUserLoginHis);
	}

	@Override
	public int count(ConsumerUserLoginHis consumerUserLoginHis) {
		return consumerUserLoginHisMapper.count(consumerUserLoginHis);
	}

	@Override
	public void deleteAll() {
		consumerUserLoginHisMapper.deleteAll();
		
	}

	@Override
	public int countDistin(ConsumerUserLoginHis consumerUserLoginHisSer) {
		return consumerUserLoginHisMapper.countDistin(consumerUserLoginHisSer);
	}
	
}