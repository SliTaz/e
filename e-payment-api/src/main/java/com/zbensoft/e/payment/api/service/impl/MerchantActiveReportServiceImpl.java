package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantActiveReportService;
import com.zbensoft.e.payment.db.domain.MerchantActiveReport;
import com.zbensoft.e.payment.db.mapper.MerchantActiveReportMapper;


@Service
public class MerchantActiveReportServiceImpl implements MerchantActiveReportService {
	@Autowired
	MerchantActiveReportMapper merchantActiveReportMapper;
	
	
	@Override
	public int deleteByPrimaryKey(String statisticsTime) {
		return merchantActiveReportMapper.deleteByPrimaryKey(statisticsTime);
	}

	@Override
	public int insert(MerchantActiveReport record) {
		return merchantActiveReportMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantActiveReport record) {
		return merchantActiveReportMapper.insertSelective(record);
	}

	@Override
	public MerchantActiveReport selectByPrimaryKey(String statisticsTime) {
		return merchantActiveReportMapper.selectByPrimaryKey(statisticsTime);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantActiveReport record) {
		return merchantActiveReportMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantActiveReport record) {
		return merchantActiveReportMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantActiveReport> selectPage(MerchantActiveReport record) {
		// TODO Auto-generated method stub
		return merchantActiveReportMapper.selectPage(record);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return merchantActiveReportMapper.deleteAll();
	}

	@Override
	public int count(MerchantActiveReport merchantActiveReport) {
		// TODO Auto-generated method stub
		return merchantActiveReportMapper.count(merchantActiveReport);
	}

}