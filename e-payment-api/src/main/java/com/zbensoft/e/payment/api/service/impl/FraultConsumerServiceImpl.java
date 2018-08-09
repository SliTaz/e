package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultConsumerService;
import com.zbensoft.e.payment.db.domain.FraultConsumer;
import com.zbensoft.e.payment.db.mapper.FraultConsumerMapper;
@Service
public class FraultConsumerServiceImpl implements FraultConsumerService {
@Autowired
FraultConsumerMapper  fraultConsumerMapper;
	@Override
	public int deleteByPrimaryKey(String fraultConsumerId) {
		
		return fraultConsumerMapper.deleteByPrimaryKey(fraultConsumerId);
	}

	@Override
	public int insert(FraultConsumer record) {
		
		return fraultConsumerMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultConsumer record) {
		
		return fraultConsumerMapper.insertSelective(record);
	}

	@Override
	public FraultConsumer selectByPrimaryKey(String fraultConsumerId) {
		
		return fraultConsumerMapper.selectByPrimaryKey(fraultConsumerId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultConsumer record) {
		
		return fraultConsumerMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultConsumer record) {
		
		return fraultConsumerMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		
		return fraultConsumerMapper.deleteAll();
	}

	@Override
	public int count(FraultConsumer card) {
		
		return fraultConsumerMapper.count(card);
	}

	@Override
	public List<FraultConsumer> selectPage(FraultConsumer record) {
		
		return fraultConsumerMapper.selectPage(record);
	}

}
