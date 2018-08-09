package com.zbensoft.e.payment.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MonthBillTransferDetailService;
import com.zbensoft.e.payment.db.domain.MonthBillTransferDetail;
import com.zbensoft.e.payment.db.mapper.MonthBillTransferDetailMapper;

@Service
public class MonthBillTransferDetailServiceImpl implements MonthBillTransferDetailService {
	@Autowired
	MonthBillTransferDetailMapper monthBillTransferDetailMapper;

	@Override
	public int deleteByPrimaryKey(MonthBillTransferDetail key) {
		return monthBillTransferDetailMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(MonthBillTransferDetail record) {
		return monthBillTransferDetailMapper.insert(record);
	}

	@Override
	public int insertSelective(MonthBillTransferDetail record) {
		return monthBillTransferDetailMapper.insertSelective(record);
	}

	@Override
	public MonthBillTransferDetail selectByPrimaryKey(MonthBillTransferDetail key) {
		return monthBillTransferDetailMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(MonthBillTransferDetail record) {
		return monthBillTransferDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MonthBillTransferDetail record) {
		return monthBillTransferDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<MonthBillTransferDetail> selectPage(MonthBillTransferDetail record) {
		return monthBillTransferDetailMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		monthBillTransferDetailMapper.deleteAll();

	}

	@Override
	public int count(MonthBillTransferDetail monthBillTransferDetail) {
		return monthBillTransferDetailMapper.count(monthBillTransferDetail);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		monthBillTransferDetailMapper.deleteByBillDate(billDate);
	}

	@Override
	public List<MonthBillTransferDetail> queryMontyBillTransferDetail(String userId, String[] months) {
		Map<String,Object> paramObject=new HashMap<String,Object>(2);
		paramObject.put("userId", userId);
		paramObject.put("months", months);
	   return monthBillTransferDetailMapper.queryMontyBillTransferDetail(paramObject);
	}

}
