package com.zbensoft.e.payment.api.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BankInterfaceStatisticsService;
import com.zbensoft.e.payment.db.domain.BankInterfaceStatistics;
import com.zbensoft.e.payment.db.domain.BankInterfaceStatisticsKey;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.mapper.BankInterfaceStatisticsMapper;

@Service
public class BankInterfaceStatisticsServiceImpl implements BankInterfaceStatisticsService {

	@Autowired
	BankInterfaceStatisticsMapper bankInterfaceStatisticsMapper;

	@Override
	public int deleteByPrimaryKey(BankInterfaceStatisticsKey key) {
		return bankInterfaceStatisticsMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(BankInterfaceStatistics record) {
		return bankInterfaceStatisticsMapper.insert(record);
	}

	@Override
	public int insertSelective(BankInterfaceStatistics record) {
		return bankInterfaceStatisticsMapper.insertSelective(record);
	}

	@Override
	public BankInterfaceStatistics selectByPrimaryKey(BankInterfaceStatisticsKey key) {
		return bankInterfaceStatisticsMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(BankInterfaceStatistics record) {
		return bankInterfaceStatisticsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(BankInterfaceStatistics record) {
		return bankInterfaceStatisticsMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<BankInterfaceStatistics> selectPage(BankInterfaceStatistics bankInterfaceStatistics) {
		return bankInterfaceStatisticsMapper.selectPage(bankInterfaceStatistics);
	}
	
	@Override
	public int count(BankInterfaceStatistics bankInterfaceStatistics) {
		return bankInterfaceStatisticsMapper.count(bankInterfaceStatistics);
	}
	
	@Override
	public List<BankInterfaceStatistics> selectPageMonth(BankInterfaceStatistics record, String type) {

		List<BankInterfaceStatistics> bankInterfaceStatisticsList = null;
		if ("MONTH".equalsIgnoreCase(type)) {
			bankInterfaceStatisticsList = bankInterfaceStatisticsMapper.selectPageByMouth(record);
		}
		return bankInterfaceStatisticsList;

	}

	@Override
	public int countMonth(BankInterfaceStatistics bankInterfaceStatistics, String type) {
		
		if("MONTH".equalsIgnoreCase(type))
		{
			return bankInterfaceStatisticsMapper.countByMouth(bankInterfaceStatistics);
		}
		
		return 0;
	
	}
	@Override
	public List<BankInterfaceStatistics> selectPageDay(BankInterfaceStatistics record, String type) {

		List<BankInterfaceStatistics> bankInterfaceStatisticsList = null;
		if ("DAY".equalsIgnoreCase(type)) {
			bankInterfaceStatisticsList = bankInterfaceStatisticsMapper.selectPageByDay(record);
		}
		return bankInterfaceStatisticsList;

	}

	@Override
	public int countDay(BankInterfaceStatistics bankInterfaceStatistics, String type) {
		
		if("DAY".equalsIgnoreCase(type))
		{
			return bankInterfaceStatisticsMapper.countByDay(bankInterfaceStatistics);
		}
		
		return 0;
	
	}
	@Override
	public List<BankInterfaceStatistics> selectPageDailyDay(BankInterfaceStatistics record, String type) {

		List<BankInterfaceStatistics> bankInterfaceStatisticsList = null;
		if ("DAY".equalsIgnoreCase(type)) {
			bankInterfaceStatisticsList = bankInterfaceStatisticsMapper.selectPageByDailyDay(record);
		}
		return bankInterfaceStatisticsList;

	}

	@Override
	public int countDailyDay(BankInterfaceStatistics bankInterfaceStatistics, String type) {
		
		if("DAY".equalsIgnoreCase(type))
		{
			return bankInterfaceStatisticsMapper.countByDailyDay(bankInterfaceStatistics);
		}
		
		return 0;
	
	}

}
