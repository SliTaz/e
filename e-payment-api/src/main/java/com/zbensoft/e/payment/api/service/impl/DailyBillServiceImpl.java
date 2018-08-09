package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.DailyBillService;
import com.zbensoft.e.payment.db.domain.DailyBill;
import com.zbensoft.e.payment.db.mapper.DailyBillMapper;

@Service
public class DailyBillServiceImpl implements DailyBillService {
	
	@Autowired
	DailyBillMapper dailyBillMapper;

	@Override
	public int deleteByPrimaryKey(DailyBill key) {
		return dailyBillMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(DailyBill record) {
		return dailyBillMapper.insert(record);
	}

	@Override
	public int insertSelective(DailyBill record) {
		return dailyBillMapper.insertSelective(record);
	}

	@Override
	public DailyBill selectByPrimaryKey(DailyBill key) {
		return dailyBillMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(DailyBill record) {
		return dailyBillMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(DailyBill record) {
		return dailyBillMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<DailyBill> selectPage(DailyBill record) {
		return dailyBillMapper.selectPage(record);
	}

	@Override
	public boolean isExist(DailyBill dailyBill) {
		return false;
	}

	@Override
	public void deleteAll() {
		dailyBillMapper.deleteAll();
	}

	@Override
	public int count(DailyBill dailyBill) {
		return dailyBillMapper.count(dailyBill);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		dailyBillMapper.deleteByBillDate(billDate);
	}
	
	
	

}
