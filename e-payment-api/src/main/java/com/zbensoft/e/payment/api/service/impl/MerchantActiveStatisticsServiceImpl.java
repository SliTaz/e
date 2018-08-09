package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantActiveStatisticsService;
import com.zbensoft.e.payment.db.domain.MerchantActiveStatistics;
import com.zbensoft.e.payment.db.mapper.MerchantActiveStatisticsMapper;
@Service
public class MerchantActiveStatisticsServiceImpl implements MerchantActiveStatisticsService {
	@Autowired
	MerchantActiveStatisticsMapper merchantActiveStatisticsMapper;
	@Override
	public int deleteByPrimaryKey(String statisticsTime) {
		return merchantActiveStatisticsMapper.deleteByPrimaryKey(statisticsTime);
	}

	@Override
	public int insert(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.insertSelective(record);
	}

	@Override
	public MerchantActiveStatistics selectByPrimaryKey(String statisticsTime) {
		return merchantActiveStatisticsMapper.selectByPrimaryKey(statisticsTime);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantActiveStatistics> selectPage(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.selectPage(record);
	}

	@Override
	public boolean isMerchantActiveStatisticsExist(MerchantActiveStatistics record) {
		return selectByPrimaryKey(record.getStatisticsTime())!= null;
	}

	@Override
	public int count(MerchantActiveStatistics record) {
		return merchantActiveStatisticsMapper.count(record);
	}

}