package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerTradeService;
import com.zbensoft.e.payment.db.domain.ConsumerTradeKey;
import com.zbensoft.e.payment.db.mapper.ConsumerTradeMapper;

@Service
public class ConsumerTradeServiceImpl implements ConsumerTradeService {

	@Autowired
	ConsumerTradeMapper consumeTradeMapper;

	@Override
	public int deleteByPrimaryKey(ConsumerTradeKey key) {
		return consumeTradeMapper.deleteByPrimaryKey(key);
	}

	@Override
	public ConsumerTradeKey selectByPrimaryKey(ConsumerTradeKey key) {
		return consumeTradeMapper.selectByPrimaryKey(key);
	}

	@Override
	public int insert(ConsumerTradeKey record) {
		return consumeTradeMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerTradeKey record) {
		return consumeTradeMapper.insertSelective(record);
	}
	@Override
	public int count(ConsumerTradeKey consumeTrade) {
		return consumeTradeMapper.count(consumeTrade);
	}
	@Override
	public int countTwo(ConsumerTradeKey consumeTrade) {
		return consumeTradeMapper.countTwo(consumeTrade);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerTradeKey record) {

		return consumeTradeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerTradeKey record) {

		return consumeTradeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerTradeKey> selectPage(ConsumerTradeKey record) {
		return consumeTradeMapper.selectPage(record);
	}
	@Override
	public List<ConsumerTradeKey> selectPageTwo(ConsumerTradeKey record) {
		return consumeTradeMapper.selectPageTwo(record);
	}

	@Override
	public void deleteAll() {
		consumeTradeMapper.deleteAll();
	}
	@Override
	public List<ConsumerTradeKey> selectByUserId(String userId) {
		return consumeTradeMapper.selectByUserId(userId);
	}

}
