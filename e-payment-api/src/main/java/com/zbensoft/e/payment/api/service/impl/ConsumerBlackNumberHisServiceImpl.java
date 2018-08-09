package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerBlackNumberHisService;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumberHis;
import com.zbensoft.e.payment.db.mapper.ConsumerBlackNumberHisMapper;

@Service
public class ConsumerBlackNumberHisServiceImpl implements ConsumerBlackNumberHisService {
	@Autowired
	ConsumerBlackNumberHisMapper consumerBlackNumberHisMapper;

	@Override
	public int deleteByPrimaryKey(String consumerBlackNumberHisId) {
		
		return consumerBlackNumberHisMapper.deleteByPrimaryKey(consumerBlackNumberHisId);
	}

	@Override
	public int insert(ConsumerBlackNumberHis record) {
		
		return consumerBlackNumberHisMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerBlackNumberHis record) {
		
		return consumerBlackNumberHisMapper.insertSelective(record);
	}

	@Override
	public ConsumerBlackNumberHis selectByPrimaryKey(String consumerBlackNumberHisId) {
		
		return consumerBlackNumberHisMapper.selectByPrimaryKey(consumerBlackNumberHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerBlackNumberHis record) {
		
		return consumerBlackNumberHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerBlackNumberHis record) {
		
		return consumerBlackNumberHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerBlackNumberHis> selectPage(ConsumerBlackNumberHis record) {
		return consumerBlackNumberHisMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumerBlackNumberHisMapper.deleteAll();
	}

	@Override
	public int count(ConsumerBlackNumberHis consumerBlackNumberHis) {
		return consumerBlackNumberHisMapper.count(consumerBlackNumberHis);
	}
	
}
