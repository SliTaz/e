package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ReconciliationBatchService;
import com.zbensoft.e.payment.db.domain.ReconciliationBatch;
import com.zbensoft.e.payment.db.mapper.ReconciliationBatchMapper;

@Service
public class ReconciliationBatchImpl implements ReconciliationBatchService {

	@Autowired
	ReconciliationBatchMapper reconciliationBatchMapper;

	@Override
	public int deleteByPrimaryKey(String reconciliationBatchId) {
		return reconciliationBatchMapper.deleteByPrimaryKey(reconciliationBatchId);
	}

	@Override
	public int insert(ReconciliationBatch record) {
		return reconciliationBatchMapper.insert(record);
	}

	@Override
	public int insertSelective(ReconciliationBatch record) {
		return reconciliationBatchMapper.insertSelective(record);
	}

	@Override
	public ReconciliationBatch selectByPrimaryKey(String reconciliationBatchId) {
		return reconciliationBatchMapper.selectByPrimaryKey(reconciliationBatchId);
	}

	@Override
	public int updateByPrimaryKeySelective(ReconciliationBatch record) {
		return reconciliationBatchMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ReconciliationBatch record) {
		return reconciliationBatchMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ReconciliationBatch> selectPage(ReconciliationBatch record) {
		return reconciliationBatchMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		reconciliationBatchMapper.deleteAll();
	}

	@Override
	public int count(ReconciliationBatch reconciliationBatch) {
		return reconciliationBatchMapper.count(reconciliationBatch);
	}

}
