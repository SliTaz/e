package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ReconciliationBankService;
import com.zbensoft.e.payment.db.domain.ReconciliationBank;
import com.zbensoft.e.payment.db.domain.ReconciliationBankKey;
import com.zbensoft.e.payment.db.mapper.ReconciliationBankMapper;

@Service
public class ReconciliationBankServiceImpl implements ReconciliationBankService {

	@Autowired
	ReconciliationBankMapper reconciliationBankMapper;

	@Override
	public int deleteByPrimaryKey(ReconciliationBankKey key) {
		return reconciliationBankMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(ReconciliationBank record) {
		return reconciliationBankMapper.insert(record);
	}

	@Override
	public int insertSelective(ReconciliationBank record) {
		return reconciliationBankMapper.insertSelective(record);
	}

	@Override
	public ReconciliationBank selectByPrimaryKey(ReconciliationBankKey key) {
		return reconciliationBankMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(ReconciliationBank record) {
		return reconciliationBankMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ReconciliationBank record) {
		return reconciliationBankMapper.updateByPrimaryKey(record);
	}
	@Override
	public List<ReconciliationBank> selectPage(ReconciliationBank record) {
		return reconciliationBankMapper.selectPage(record);
	}

	@Override
	public int count(ReconciliationBank reconciliationBank) {
		return reconciliationBankMapper.count(reconciliationBank);
	}
}
