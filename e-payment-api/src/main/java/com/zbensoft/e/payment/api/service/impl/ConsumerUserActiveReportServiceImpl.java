package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerUserActiveReportService;
import com.zbensoft.e.payment.db.domain.ConsumerUserActiveReport;
import com.zbensoft.e.payment.db.domain.MerchantActiveReport;
import com.zbensoft.e.payment.db.mapper.ConsumerUserActiveReportMapper;

@Service
public class ConsumerUserActiveReportServiceImpl implements ConsumerUserActiveReportService {
	@Autowired
	ConsumerUserActiveReportMapper consumerUserActiveReportMapper;
	@Override
	public int deleteByPrimaryKey(String statisticsTime) {
		return consumerUserActiveReportMapper.deleteByPrimaryKey(statisticsTime);
	}

	@Override
	public int insert(ConsumerUserActiveReport record) {
		return consumerUserActiveReportMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerUserActiveReport record) {
		return consumerUserActiveReportMapper.insertSelective(record);
	}

	@Override
	public ConsumerUserActiveReport selectByPrimaryKey(String statisticsTime) {
		return consumerUserActiveReportMapper.selectByPrimaryKey(statisticsTime);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerUserActiveReport record) {
		return consumerUserActiveReportMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerUserActiveReport record) {
		return consumerUserActiveReportMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUserActiveReport> selectPage(ConsumerUserActiveReport record) {
		// TODO Auto-generated method stub
		return consumerUserActiveReportMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return consumerUserActiveReportMapper.deleteAll();
	}

	@Override
	public int count(ConsumerUserActiveReport customerActiveReport) {
		// TODO Auto-generated method stub
		return consumerUserActiveReportMapper.count(customerActiveReport);
	}





}