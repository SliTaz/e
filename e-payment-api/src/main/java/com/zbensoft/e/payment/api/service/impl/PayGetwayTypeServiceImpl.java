package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.PayGetwayTypeService;
import com.zbensoft.e.payment.db.domain.PayGetwayType;
import com.zbensoft.e.payment.db.mapper.PayGetwayTypeMapper;

@Service
public class PayGetwayTypeServiceImpl implements PayGetwayTypeService {

	@Autowired
	PayGetwayTypeMapper payGetwayTypeMapper;
	
	@Override
	public int deleteByPrimaryKey(String payGatewayTypeId) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.deleteByPrimaryKey(payGatewayTypeId);
	}

	@Override
	public int insert(PayGetwayType record) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(PayGetwayType record) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.insertSelective(record);
	}

	@Override
	public PayGetwayType selectByPrimaryKey(String payGatewayTypeId) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.selectByPrimaryKey(payGatewayTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(PayGetwayType record) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(PayGetwayType record) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<PayGetwayType> selectPage(PayGetwayType record) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.selectPage(record);
	}

	@Override
	public boolean isPayGetwayTypeExist(PayGetwayType payGetwayType) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.findByName(payGetwayType.getName())!=null;
	}

	@Override
	public int count(PayGetwayType payGetwayType) {
		// TODO Auto-generated method stub
		return payGetwayTypeMapper.count(payGetwayType);
	}

}
