package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerUserActiveStatisticsService;
import com.zbensoft.e.payment.db.domain.ConsumerUserActiveStatistics;
import com.zbensoft.e.payment.db.mapper.ConsumerUserActiveStatisticsMapper;

@Service
public class ConsumerUserActiveStatisticsServiceImpl implements ConsumerUserActiveStatisticsService {
	@Autowired
	ConsumerUserActiveStatisticsMapper consumerUserActiveStatisticsMapper;
	@Override
	public int deleteByPrimaryKey(String statisticsTime) {
		return consumerUserActiveStatisticsMapper.deleteByPrimaryKey(statisticsTime);
	}

	@Override
	public int insert(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.insertSelective(record);
	}

	@Override
	public ConsumerUserActiveStatistics selectByPrimaryKey(String statisticsTime) {
		return consumerUserActiveStatisticsMapper.selectByPrimaryKey(statisticsTime);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUserActiveStatistics> selectPage(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.selectPage(record);
	}

	@Override
	public int count(ConsumerUserActiveStatistics record) {
		return consumerUserActiveStatisticsMapper.count(record);
	}

	@Override
	public boolean isConsumerUserActiveStatisticsExist(ConsumerUserActiveStatistics record) {
		return selectByPrimaryKey(record.getStatisticsTime())!= null;
	}

}