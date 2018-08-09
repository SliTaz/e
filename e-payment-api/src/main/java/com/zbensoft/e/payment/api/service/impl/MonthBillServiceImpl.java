package com.zbensoft.e.payment.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MonthBillService;
import com.zbensoft.e.payment.db.domain.MonthBill;
import com.zbensoft.e.payment.db.mapper.MonthBillMapper;

@Service
public class MonthBillServiceImpl implements MonthBillService {

	@Autowired
	MonthBillMapper monthBillMapper;

	@Override
	public int deleteByPrimaryKey(MonthBill key) {
		return monthBillMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(MonthBill record) {
		return monthBillMapper.insert(record);
	}

	@Override
	public int insertSelective(MonthBill record) {
		return monthBillMapper.insertSelective(record);
	}

	@Override
	public MonthBill selectByPrimaryKey(MonthBill key) {
		return monthBillMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(MonthBill record) {
		return monthBillMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MonthBill record) {
		return monthBillMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MonthBill> selectPage(MonthBill record) {
		return monthBillMapper.selectPage(record);
	}

	@Override
	public boolean isExist(MonthBill monthBill) {
		return false;
	}

	@Override
	public void deleteAll() {
		monthBillMapper.deleteAll();
	}

	@Override
	public int count(MonthBill monthBill) {
		return monthBillMapper.count(monthBill);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		monthBillMapper.deleteByBillDate(billDate);
	}

	@Override
	public List<MonthBill> queryLastFiveMonth(String userId, String []  monthArr) {
		
		Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("userId", userId);
        params.put("monthArr", monthArr);
	    return	monthBillMapper.queryLastFiveMonth(params);
	}


}
