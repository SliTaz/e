package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.mapper.ConsumerCouponMapper;

@Service
public class ConsumerCouponServiceImpl implements ConsumerCouponService {
	@Autowired
	ConsumerCouponMapper consumerCouponMapper;

	@Override
	public int deleteByPrimaryKey(ConsumerCoupon key) {
		return consumerCouponMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerCoupon record) {
		return consumerCouponMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerCoupon record) {
		return consumerCouponMapper.insertSelective(record);
	}

	@Override
	public ConsumerCoupon selectByPrimaryKey(ConsumerCoupon key) {
		return consumerCouponMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerCoupon record) {
		return consumerCouponMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerCoupon record) {
		return consumerCouponMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<ConsumerCoupon> selectPage(ConsumerCoupon record) {
		return consumerCouponMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		 consumerCouponMapper.deleteAll();
		
	}

	@Override
	public int count(ConsumerCoupon consumerCoupon) {
		return consumerCouponMapper.count(consumerCoupon);
	}

	@Override
	public List<ConsumerCoupon> selectByClapId(String consumerUserClapId) {
		return consumerCouponMapper.selectByClapId(consumerUserClapId);
	}

	@Override
	public void updateByStatus(ConsumerCoupon consumerCoupon) {
		consumerCouponMapper.updateByStatus(consumerCoupon);
		
	}

	
}
