package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BalanceStatisticsService;
import com.zbensoft.e.payment.db.domain.BalanceStatistics;
import com.zbensoft.e.payment.db.mapper.BalanceStatisticsMapper;

@Service
public class BalanceStatisticsServiceImpl implements BalanceStatisticsService {
	@Autowired
	BalanceStatisticsMapper balanceStatisticsMapper;

	@Override
	public int deleteByPrimaryKey(String time) {

		return balanceStatisticsMapper.deleteByPrimaryKey(time);
	}

	@Override
	public int insert(BalanceStatistics record) {

		return balanceStatisticsMapper.insert(record);
	}

	@Override
	public int insertSelective(BalanceStatistics record) {

		return balanceStatisticsMapper.insertSelective(record);
	}

	@Override
	public BalanceStatistics selectByPrimaryKey(String time) {

		return balanceStatisticsMapper.selectByPrimaryKey(time);
	}

	@Override
	public int updateByPrimaryKeySelective(BalanceStatistics record) {

		return balanceStatisticsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(BalanceStatistics record) {

		return balanceStatisticsMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<BalanceStatistics> selectPage(BalanceStatistics record) {
		return balanceStatisticsMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		balanceStatisticsMapper.deleteAll();

	}

	@Override
	public int count(BalanceStatistics balanceStatistics) {
		return balanceStatisticsMapper.count(balanceStatistics);
	}
}
