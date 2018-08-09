package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.DailyBillTransferDetailService;
import com.zbensoft.e.payment.db.domain.DailyBillTransferDetail;
import com.zbensoft.e.payment.db.mapper.DailyBillTransferDetailMapper;

@Service
public class DailyBillTransferDetailServiceImpl implements DailyBillTransferDetailService {
	@Autowired
	DailyBillTransferDetailMapper dailyBillTransferDetailMapper;

	@Override
	public int deleteByPrimaryKey(DailyBillTransferDetail key) {
		return dailyBillTransferDetailMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(DailyBillTransferDetail record) {
		return dailyBillTransferDetailMapper.insert(record);
	}

	@Override
	public int insertSelective(DailyBillTransferDetail record) {
		return dailyBillTransferDetailMapper.insertSelective(record);
	}

	@Override
	public DailyBillTransferDetail selectByPrimaryKey(DailyBillTransferDetail key) {
		return dailyBillTransferDetailMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(DailyBillTransferDetail record) {
		return dailyBillTransferDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(DailyBillTransferDetail record) {
		return dailyBillTransferDetailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<DailyBillTransferDetail> selectPage(DailyBillTransferDetail record) {
		return dailyBillTransferDetailMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		 dailyBillTransferDetailMapper.deleteAll();
		
	}

	@Override
	public int count(DailyBillTransferDetail dailyBillTransferDetail) {
		return dailyBillTransferDetailMapper.count(dailyBillTransferDetail);
	}

	@Override
	public void deleteByBillDate(String billDate) {
		dailyBillTransferDetailMapper.deleteByBillDate(billDate);
	}

	
}
