package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.InterfaceStatisticsService;
import com.zbensoft.e.payment.db.domain.BankInterfaceStatistics;
import com.zbensoft.e.payment.db.domain.InterfaceStatistics;
import com.zbensoft.e.payment.db.domain.InterfaceStatisticsKey;
import com.zbensoft.e.payment.db.mapper.InterfaceStatisticsMapper;

@Service
public class InterfaceStatisticsServiceImpl implements InterfaceStatisticsService {

	@Autowired
	InterfaceStatisticsMapper interfaceStatisticsMapper;

	@Override
	public int deleteByPrimaryKey(InterfaceStatisticsKey key) {
		return interfaceStatisticsMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(InterfaceStatistics record) {
		return interfaceStatisticsMapper.insert(record);
	}

	@Override
	public int insertSelective(InterfaceStatistics record) {
		return interfaceStatisticsMapper.insertSelective(record);
	}

	@Override
	public InterfaceStatistics selectByPrimaryKey(InterfaceStatisticsKey key) {
		return interfaceStatisticsMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(InterfaceStatistics record) {
		return interfaceStatisticsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(InterfaceStatistics record) {
		return interfaceStatisticsMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<InterfaceStatistics> selectPage(InterfaceStatistics record) {
		return interfaceStatisticsMapper.selectPage(record);
	}

	@Override
	public int count(InterfaceStatistics interfaceStatistics) {
		return interfaceStatisticsMapper.count(interfaceStatistics);
	}
	
	@Override
	public List<InterfaceStatistics> selectPageDay(InterfaceStatistics record, String type) {

		List<InterfaceStatistics> interfaceStatisticsList = null;
		if ("DAY".equalsIgnoreCase(type)) {
			interfaceStatisticsList = interfaceStatisticsMapper.selectPageByDay(record);
		}
		return interfaceStatisticsList;

	}

	@Override
	public int countDay(InterfaceStatistics interfaceStatistics, String type) {
		
		if("DAY".equalsIgnoreCase(type))
		{
			return interfaceStatisticsMapper.countByDay(interfaceStatistics);
		}
		
		return 0;
	
	}


}
