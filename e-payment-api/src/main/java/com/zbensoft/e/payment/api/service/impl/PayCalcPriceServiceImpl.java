package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayCalcPriceService;
import com.zbensoft.e.payment.db.domain.PayApp;
import com.zbensoft.e.payment.db.domain.PayCalcPrice;
import com.zbensoft.e.payment.db.mapper.PayCalcPriceMapper;

@Service
public class PayCalcPriceServiceImpl implements PayCalcPriceService {
	@Autowired
	PayCalcPriceMapper payCalcPriceMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {

		return payCalcPriceMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(PayCalcPrice record) {

		return payCalcPriceMapper.insert(record);
	}

	@Override
	public int insertSelective(PayCalcPrice record) {

		return payCalcPriceMapper.insertSelective(record);
	}

	@Override
	public PayCalcPrice selectByPrimaryKey(String bankId) {

		return payCalcPriceMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(PayCalcPrice record) {

		return payCalcPriceMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayCalcPrice record) {

		return payCalcPriceMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayCalcPrice> selectPage(PayCalcPrice record) {
		return payCalcPriceMapper.selectPage(record);
	}

	@Override
	public boolean isPayCalcPriceExist(PayCalcPrice payCalcPrice) {
		return findByName(payCalcPrice.getName()) != null;
	}

	private PayCalcPrice findByName(String name) {
		return payCalcPriceMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		payCalcPriceMapper.deleteAll();
	}

	@Override
	public int count(PayCalcPrice payCalcPrice) {
		return payCalcPriceMapper.count(payCalcPrice);
	}
}
