package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MonthBillConsumptionDetailService;
import com.zbensoft.e.payment.db.domain.MonthBillConsumptionDetail;
import com.zbensoft.e.payment.db.mapper.MonthBillConsumptionDetailMapper;

@Service
public class MonthBillConsumptionDetailServiceImpl implements MonthBillConsumptionDetailService {
	@Autowired
	MonthBillConsumptionDetailMapper monthBillConsumptionDetailMapper;

	@Override
	public int deleteByPrimaryKey(MonthBillConsumptionDetail key) {
		return monthBillConsumptionDetailMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(MonthBillConsumptionDetail record) {
		return monthBillConsumptionDetailMapper.insert(record);
	}

	@Override
	public int insertSelective(MonthBillConsumptionDetail record) {
		return monthBillConsumptionDetailMapper.insertSelective(record);
	}

	@Override
	public MonthBillConsumptionDetail selectByPrimaryKey(MonthBillConsumptionDetail key) {
		return monthBillConsumptionDetailMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(MonthBillConsumptionDetail record) {
		return monthBillConsumptionDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MonthBillConsumptionDetail record) {
		return monthBillConsumptionDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<MonthBillConsumptionDetail> selectPage(MonthBillConsumptionDetail record) {
		return monthBillConsumptionDetailMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		monthBillConsumptionDetailMapper.deleteAll();

	}

	@Override
	public int count(MonthBillConsumptionDetail monthBillConsumptionDetail) {
		return monthBillConsumptionDetailMapper.count(monthBillConsumptionDetail);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		monthBillConsumptionDetailMapper.deleteByBillDate(billDate);
	}

	@Override
	public List<MonthBillConsumptionDetail> queryMonthDetailDetails(String userId,String month) {
		return monthBillConsumptionDetailMapper.queryMonthDetailDetails(userId,month);
	}

}
