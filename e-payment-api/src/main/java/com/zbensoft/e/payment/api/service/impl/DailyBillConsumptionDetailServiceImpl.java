package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.DailyBillConsumptionDetailService;
import com.zbensoft.e.payment.db.domain.DailyBillConsumptionDetail;
import com.zbensoft.e.payment.db.mapper.DailyBillConsumptionDetailMapper;

@Service
public class DailyBillConsumptionDetailServiceImpl implements DailyBillConsumptionDetailService {
	@Autowired
	DailyBillConsumptionDetailMapper dailyBillConsumptionDetailMapper;

	@Override
	public int deleteByPrimaryKey(DailyBillConsumptionDetail key) {
		return dailyBillConsumptionDetailMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(DailyBillConsumptionDetail record) {
		return dailyBillConsumptionDetailMapper.insert(record);
	}

	@Override
	public int insertSelective(DailyBillConsumptionDetail record) {
		return dailyBillConsumptionDetailMapper.insertSelective(record);
	}

	@Override
	public DailyBillConsumptionDetail selectByPrimaryKey(DailyBillConsumptionDetail key) {
		return dailyBillConsumptionDetailMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(DailyBillConsumptionDetail record) {
		return dailyBillConsumptionDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(DailyBillConsumptionDetail record) {
		return dailyBillConsumptionDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<DailyBillConsumptionDetail> selectPage(DailyBillConsumptionDetail record) {
		return dailyBillConsumptionDetailMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		dailyBillConsumptionDetailMapper.deleteAll();

	}

	@Override
	public int count(DailyBillConsumptionDetail dailyBillConsumptionDetail) {
		return dailyBillConsumptionDetailMapper.count(dailyBillConsumptionDetail);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		dailyBillConsumptionDetailMapper.deleteByBillDate(billDate);
	}

}
