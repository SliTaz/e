package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerUserBankCardService;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.mapper.ConsumerUserBankCardMapper;

@Service
public class ConsumerUserBankCardServiceImpl implements ConsumerUserBankCardService {

	@Autowired
	ConsumerUserBankCardMapper consumerUserBankCardMapper;

	@Override
	public int deleteByPrimaryKey(String bankBindId) {
		return consumerUserBankCardMapper.deleteByPrimaryKey(bankBindId);
	}

	@Override
	public int insert(ConsumerUserBankCard record) {
		return consumerUserBankCardMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerUserBankCard record) {
		return consumerUserBankCardMapper.insertSelective(record);
	}

	@Override
	public ConsumerUserBankCard selectByPrimaryKey(String bankBindId) {
		return consumerUserBankCardMapper.selectByPrimaryKey(bankBindId);
	}
	@Override
	public ConsumerUserBankCard selectByCardNo(String cardNo) {
		return consumerUserBankCardMapper.selectByCardNo(cardNo);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerUserBankCard record) {
		return consumerUserBankCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerUserBankCard record) {
		return consumerUserBankCardMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUserBankCard> selectPage(ConsumerUserBankCard record) {
		return consumerUserBankCardMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumerUserBankCardMapper.deleteAll();
	}

	@Override
	public int count(ConsumerUserBankCard consumerUserBankCard) {
		return consumerUserBankCardMapper.count(consumerUserBankCard);
	}

	@Override
	public List<ConsumerUserBankCard> selectByUserId(String userId) {
		return consumerUserBankCardMapper.selectByUserId(userId);
	}

	@Override
	public ConsumerUserBankCard selectByUserIdBankCard(ConsumerUserBankCard consumerUserBankCardSer) {
		return consumerUserBankCardMapper.selectByUserIdBankCard(consumerUserBankCardSer);
	}

}
