package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.CouponBuyService;
import com.zbensoft.e.payment.db.domain.CouponBuy;
import com.zbensoft.e.payment.db.mapper.CouponBuyMapper;

@Service
public class CouponBuyServiceImpl implements CouponBuyService {
	@Autowired
	CouponBuyMapper couponBuyMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {

		return couponBuyMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(CouponBuy record) {

		return couponBuyMapper.insert(record);
	}

	@Override
	public int insertSelective(CouponBuy record) {

		return couponBuyMapper.insertSelective(record);
	}

	@Override
	public CouponBuy selectByPrimaryKey(String bankId) {

		return couponBuyMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(CouponBuy record) {

		return couponBuyMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(CouponBuy record) {

		return couponBuyMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<CouponBuy> selectPage(CouponBuy record) {
		return couponBuyMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		couponBuyMapper.deleteAll();
	}

	@Override
	public int count(CouponBuy couponBuy) {
		return couponBuyMapper.count(couponBuy);
	}
}
