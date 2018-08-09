package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyCouponMapper;

@Service
public class ConsumerFamilyCouponServiceImpl implements ConsumerFamilyCouponService {
	@Autowired
	ConsumerFamilyCouponMapper consumerFamilyCouponMapper;

	@Override
	public int deleteByPrimaryKey(ConsumerFamilyCoupon key) {
		return consumerFamilyCouponMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerFamilyCoupon record) {
		return consumerFamilyCouponMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerFamilyCoupon record) {
		return consumerFamilyCouponMapper.insertSelective(record);
	}

	@Override
	public ConsumerFamilyCoupon selectByPrimaryKey(ConsumerFamilyCoupon key) {
		return consumerFamilyCouponMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerFamilyCoupon record) {
		return consumerFamilyCouponMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerFamilyCoupon record) {
		return consumerFamilyCouponMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<ConsumerFamilyCoupon> selectPage(ConsumerFamilyCoupon record) {
		return consumerFamilyCouponMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		 consumerFamilyCouponMapper.deleteAll();
		
	}

	@Override
	public int count(ConsumerFamilyCoupon consumerFamilyCoupon) {
		return consumerFamilyCouponMapper.count(consumerFamilyCoupon);
	}

	@Override
	public List<ConsumerFamilyCoupon> selectByFamilyId(String familyId) {
		return consumerFamilyCouponMapper.selectByFamilyId(familyId);
	}

	@Override
	public void updateByStatus(ConsumerFamilyCoupon consumerFamilyCoupon) {
		consumerFamilyCouponMapper.updateByStatus(consumerFamilyCoupon);
		
	}

	@Override
	public void deleteByCouponId(ConsumerFamilyCoupon ConsumerFamilyCoupon) {
		consumerFamilyCouponMapper.deleteByCouponId(ConsumerFamilyCoupon);
	}

	
}
