package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultBankCardService;
import com.zbensoft.e.payment.db.domain.FraultBankCard;
import com.zbensoft.e.payment.db.mapper.FraultBankCardMapper;
@Service
public class FraultBankCardServiceImpl implements FraultBankCardService {
@Autowired
FraultBankCardMapper fraultBankCardMapper;
	@Override
	public int deleteByPrimaryKey(String fraultBankCardId) {
		return fraultBankCardMapper.deleteByPrimaryKey(fraultBankCardId);
	}

	@Override
	public int insert(FraultBankCard record) {
		return fraultBankCardMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultBankCard record) {
		return fraultBankCardMapper.insertSelective(record);
	}

	@Override
	public FraultBankCard selectByPrimaryKey(String fraultBankCardId) {
		return fraultBankCardMapper.selectByPrimaryKey(fraultBankCardId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultBankCard record) {
		return fraultBankCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultBankCard record) {
		return fraultBankCardMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return fraultBankCardMapper.deleteAll();
	}

	@Override
	public int count(FraultBankCard card) {
		return fraultBankCardMapper.count(card);
	}

	@Override
	public List<FraultBankCard> selectPage(FraultBankCard record) {
		return fraultBankCardMapper.selectPage(record);
	}

}
